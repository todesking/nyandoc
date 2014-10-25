package com.todesking.dox

object LibGlobal {
  def errorUnknown(name:String, unk:String) =
    throw new RuntimeException(s"Unknown ${name}: ${unk}")
}
import LibGlobal._

sealed trait Id {
  def asFileName():String = fullName
  def isParentOf(id:Id):Boolean
  def isAncestorOf(id:Id):Boolean
  def fullName():String
  def childFullName(name:String):String
  def relNameFrom(base:Id):String
}


object Id {
  sealed abstract class Root extends Id {
    override def fullName = ""
    override def isParentOf(id:Id):Boolean = id match {
      case child:Child => child.parentId == this
      case _:Root => false
    }
    override def isAncestorOf(id:Id):Boolean = id match {
      case _:Root => false
      case _ => true
    }
    override def childFullName(name:String) = name
    override def relNameFrom(base:Id):String = base match {
      case _:Root => fullName
      case _:Child => throw new IllegalArgumentException()
    }
  }
  sealed abstract class Child extends Id {
    val parentId:Id
    val localName:String

    def changeParent(parent:Id):Child

    override def fullName =
      parentId.childFullName(localName)
    override def isParentOf(id:Id):Boolean = id match {
      case child:Child => child.parentId == this
      case _:Root => false
    }
    override def isAncestorOf(id:Id):Boolean =
      id match {
        case child:Child =>
          isParentOf(child) || isAncestorOf(child.parentId)
        case _:Root => false
      }
    override def relNameFrom(base:Id):String = {
      require(base.isAncestorOf(this))
      base match {
        case _:Root => fullName
        case _:Child => fullName.substring(base.fullName.length)
      }
    }
  }

  sealed trait Type extends Id {
    override def childFullName(name:String) =
      fullName + "#" + name
  }
  sealed trait Value extends Id {
    override def childFullName(name:String) =
      fullName + "." + name
  }

  // theres no RootTypeId
  object Root extends Root with Value {
    override def childFullName(name:String) = super[Root].childFullName(name)
  }

  sealed case class ChildValue(
    override val parentId:Id,
    override val localName:String
  ) extends Child with Value {
    override def changeParent(newParent:Id) =
      ChildValue(newParent, localName)
  }

  sealed case class ChildType(
    override val parentId:Id,
    override val localName:String
  ) extends Child with Type {
    override def changeParent(newParent:Id) =
      ChildType(newParent, localName)
  }

  def Type(fullName:CharSequence):ChildType =
    build(fullName)(ChildType.apply)
  def Value(fullName:CharSequence):ChildValue =
    build(fullName)(ChildValue.apply)

  private def build[A <: Id](fullName:CharSequence)(builder:(Id, String) => A):A =
    """([.#])([^.#]+)$""".r.findFirstMatchIn(fullName) match {
      case Some(m) =>
        if(m.group(1) == ".") builder(Value(m.before), m.group(2))
        else builder(Type(m.before), m.group(2))
      case None =>
        builder(Root, fullName.toString)
    }
}

sealed class ItemKind
object ItemKind {
  object Type extends ItemKind
  object Value extends ItemKind
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

sealed abstract class Item(val id:Id)

sealed class Value(id:Id.Value/*, tpe:TypeInstance*/) extends Item(id)

case class Type(override val id:Id.Type) extends Item(id)

case class Object(override val id:Id.Value) extends Value(id) with ValueContainer
case class DefinedMethod(override val id:Id.Value) extends Value(id)
case class ViaImplicitMethod(override val id:Id.Value, originalId:Id.Value) extends Value(id)
case class ViaInheritMethod(override val id:Id.Value, originalId:Id.Value) extends Value(id)

case class Package(override val id:Id.Value) extends Value(id)

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
    val id2item:Map[Id, Item] =
      items.map{item => item.id -> item}.toMap
    new Repository {
      override def parentOf(item:Item):Option[Item] =
        item.id match {
          case Id.Root => None
          case child:Id.Child => id2item.get(child.parentId)
        }
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
        case "trait" | "class" => new Type(Id.Type(s"${ns}.${name}"))
        case "object" => Object(Id.Value(s"${ns}.${name}$$"))
        case unk => errorUnknown("TypeKind", unk)
      }
    entity +: extractValueMembers(entity.id, doc)
  }

  def extractNS(doc:Document):String = {
    doc / "#definition #owner a.extype" last() attr("name")
  }

  def extractValueMembers(parentId:Id, doc:Document):Seq[Item] = {
    (doc / "#values > ol > li").map {elm =>
      val id = Id.Value(elm.attr("name"))
      val kind = ValueKind.fromName(
        elm / ".signature > .modifier_kind > .kind" first() text())
      kind match {
        case ValueKind.Def | ValueKind.Val | ValueKind.Var =>
          if(parentId.isParentOf(id))
            DefinedMethod(id)
          else if((elm / ".signature > .symbol > .implicit").nonEmpty)
            ViaImplicitMethod(id.changeParent(parentId), id)
          else
            ViaInheritMethod(id.changeParent(parentId), id)
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
      appendln(child.id.relNameFrom(item.id))

      child match {
        case c:ViaImplicitMethod =>
          appendln(s"    (added by implicit convertion: ${c.originalId})")
        case c:ViaInheritMethod =>
          appendln(s"    (defined at ${c.originalId})")
        case _ =>
      }
    }

    sb.toString
  }
}
