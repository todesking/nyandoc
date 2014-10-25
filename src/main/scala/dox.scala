package com.todesking.dox

object LibGlobal {
  def errorUnknown(name:String, unk:String) =
    throw new RuntimeException(s"Unknown ${name}: ${unk}")
}
import LibGlobal._

case class Identifier(fullName:String) {
  def asFileName:String = fullName
  def parentId:Identifier =
    Identifier(fullName.replaceAll("""[.#][^.#]+$""", ""))
  def isParentOf(id:Identifier) =
    id.parentId == this
  def isAncestorOf(id:Identifier):Boolean =
    id.parentId == this || id.parentId.isAncestorOf(this)
  def nameFrom(parentId:Identifier):String = {
    require(parentId.isAncestorOf(this))
    fullName.substring(parentId.fullName.length)
  }
}

sealed class TypeKind
object TypeKind {
  case object Trait extends TypeKind
  case object Class extends TypeKind
  case object Type extends TypeKind
}

sealed abstract class ValueKind
object ValueKind {
  case object Val extends ValueKind
  case object Var extends ValueKind
  case object Object extends ValueKind
  case object Def extends ValueKind

  def fromName(name:String):ValueKind = {
    name match {
      case "val" => Val
      case "var" => Var
      case "object" => Object
      case "def" => Def
      case unk => errorUnknown("ValueKind", unk)
    }
  }
}

sealed class Scope
object Scope {
  case class Private() extends Scope
  case class Protected() extends Scope
  case class Public() extends Scope
}

class TypeParams
class MethodParams

trait ValueContainer {
}

sealed abstract class Item(val id:Identifier)

sealed class Value(id:Identifier/*, tpe:TypeInstance*/) extends Item(id)

case class Type(override val id:Identifier) extends Item(id)

case class Object(override val id:Identifier) extends Value(id) with ValueContainer
case class Method(override val id:Identifier) extends Value(id)

case class Package(override val id:Identifier) extends Value(id)

trait Repository {
  def isTopLevel(item:Item) = {
    parentOf(item) match {
      case Some(_:Package) => true
      case Some(_) => false
      case None => true
    }
  }

  def parentOf(item:Item):Option[Item]
  def childrenOf(item:Item):Seq[Item]
}

object Repository {
  def apply(items:Seq[Item]):Repository = {
    val id2item:Map[Identifier, Item] =
      items.map{item => item.id -> item}.toMap
    new Repository {
      override def parentOf(item:Item):Option[Item] =
        id2item.get(item.id.parentId)
      override def childrenOf(item:Item):Seq[Item] =
        id2item.filter {case (id, _) => item.id.isParentOf(id) }.map{case (_, item) => item}.toSeq
    }
  }
}


object Crawler {
  def crawl(rootUrl:java.net.URL):Seq[Item] = ???
  def crawlLocal(root:java.io.File):Seq[Item] = ???
}

object JsoupExt {
  import org.jsoup.select.Elements
  import org.jsoup.nodes.Element
  import scala.collection.JavaConverters._

  import scala.language.implicitConversions

  implicit class ElementExt[A <: Element](self:A) {
    def /(query:String) = self.select(query)
  }

  implicit class ElementsExt[A <: Elements](self:A) {
    def /(query:String) = self.select(query)
  }

  implicit def Elements2Collection(self:Elements) = self.asScala
}

object HtmlParser {
  import java.io.File
  import java.nio.charset.{Charset, StandardCharsets}
  import org.jsoup.Jsoup
  import org.jsoup.nodes.Document

  import JsoupExt._

  def parse(file:File, charset:Charset = StandardCharsets.UTF_8):Seq[Item] = {
    parse(Jsoup.parse(file, charset.name))
  }

  def parse(doc:Document):Seq[Item] = {
    // TODO: Type params
    // TODO: Linear supertypes
    // TODO: Known subclasses
    val name:String = doc / "#definition > h1" text()
    val ns:String = extractNS(doc)
    val kind:String = doc / "#signature > .modifier_kind > .kind" first() text()
    val entity =
      kind match {
        case "trait" | "class" => new Type(Identifier(s"${ns}.${name}"))
        case "object" => Object(Identifier(s"${ns}.${name}$$"))
        case unk => errorUnknown("TypeKind", unk)
      }
    Seq(entity) ++ extractValueMembers(doc)
  }

  def extractNS(doc:Document):String = {
    doc / "#definition #owner a.extype" last() attr("name")
  }

  def extractValueMembers(doc:Document):Seq[Item] = {
    (doc / "#values > ol > li").map {elm =>
      val id = Identifier(elm.attr("name"))
      val kind = ValueKind.fromName(
        elm / ".signature .modifier_kind .kind" first() text())
      kind match {
        case ValueKind.Def | ValueKind.Val | ValueKind.Var => Method(id)
        case ValueKind.Object => Object(id)
      }
    }
  }
}

object Main {
  import java.io.File

  def main(args:Array[String]):Unit = {
    if(args.size != 2) {
      println("USAGE: $0 <scaladoc-dir|scaladoc-html> <dest-dir>")
    } else {
      val src = new File(args(0))
      val dest = new File(args(1))

      val items = parse(src)
      val repo = Repository(items)

      generate(items, repo, dest)
    }
  }

  def parse(file:File):Seq[Item] = {
    if(file.isDirectory) {
      file.listFiles.flatMap(parse(_))
    } else {
      parse0(file)
    }
  }

  def parse0(file:File):Seq[Item] = {
    HtmlParser.parse(file)
  }

  def generate(items:Seq[Item], repo:Repository, dest:File):Unit = {
    if(!dest.exists)
      dest.mkdirs()

    items.foreach {item =>
      if(repo.isTopLevel(item))
        generate0(item, repo, dest)
    }
  }

  def generate0(top:Item, repo:Repository, destDir:File):Unit = {
    val content = new PlainTextFormatter().format(top, repo)

    import java.io._
    val dest:File = new File(destDir, top.id.asFileName)
    println()
    println(s"===================== ${dest} ========================")
    println(content)
    return

    val writer = new BufferedWriter(new FileWriter(dest))
    try {
      writer.write(content)
    } finally {
      writer.close()
    }
  }
}

class PlainTextFormatter {
  def format(item:Item, repo:Repository):String = {
    val sb = new scala.collection.mutable.StringBuilder

    def appendln(str:String):Unit = {sb.append(str); sb.append("\n")}
    def ln():Unit = appendln("")

    appendln(item.id.fullName)

    repo.childrenOf(item).foreach {child =>
      assert(item.id.isParentOf(child.id))

      sb.append(" - ")
      appendln(child.id.nameFrom(item.id))
    }

    sb.toString
  }
}
