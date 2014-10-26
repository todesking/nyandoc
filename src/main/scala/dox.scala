package com.todesking.dox

import scala.collection.JavaConverters._

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
  def localName():String
  def childFullName(name:String):String
  def relNameFrom(base:Id):String
  override def toString = fullName
}


object Id {
  sealed abstract class Root extends Id {
    override def fullName = ""
    override def localName = ""
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
    override val localName:String

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

sealed class TypeKind(val signature:String)
object TypeKind {
  case object Trait extends TypeKind("trait")
  case object Class extends TypeKind("class")
  case object Type extends TypeKind("type")

  def forName(name:String):TypeKind = name match {
    case "trait" => Trait
    case "class" => Class
    case "type" => Type
  }
}

sealed abstract class ValueKind
object ValueKind {
  case object Val extends ValueKind
  case object Var extends ValueKind
  case object Object extends ValueKind
  case object Def extends ValueKind

  def forName(name:String):ValueKind = {
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

case class TypeParams(signature:String)
case class MethodParams(signature:String)
case class ResultType(signature:String)

sealed abstract class Markup
object Markup {
  case class Text(content:String) extends Markup
  case class Paragraph(children:Seq[Markup]) extends Markup
  case class Dl(items:Seq[DlItem]) extends Markup
  case class DlItem(dt:Seq[Markup], dd:Seq[Markup])
}

sealed abstract class Item(val id:Id, val comment:Seq[Markup]) {
  def signature:String = id.localName
}

sealed class Value(id:Id.Value, comment:Seq[Markup]) extends Item(id, comment)

case class Type(override val id:Id.Type, kind:TypeKind, params:TypeParams, override val comment:Seq[Markup]) extends Item(id, comment) {
  override def signature:String =
    kind.signature + " " + id.localName + params.signature
}

case class Object(override val id:Id.Value, override val comment:Seq[Markup]) extends Value(id, comment)
abstract class Method(id:Id.Value, params:MethodParams, resultType:ResultType, comment:Seq[Markup]) extends Value(id, comment) {
  override def signature = s"def ${id.localName}${params.signature}: ${resultType.signature}"
}
case class DefinedMethod(override val id:Id.Value, params:MethodParams, resultType:ResultType, override val comment:Seq[Markup]) extends Method(id, params, resultType, comment)
case class ViaImplicitMethod(override val id:Id.Value, params:MethodParams, resultType:ResultType, originalId:Id.Value, override val comment:Seq[Markup]) extends Method(id, params, resultType, comment)
case class ViaInheritMethod(override val id:Id.Value, params:MethodParams, resultType:ResultType, originalId:Id.Value, override val comment:Seq[Markup]) extends Method(id, params, resultType, comment)

case class Package(override val id:Id.Value, override val comment:Seq[Markup]) extends Value(id, comment)

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
    def firstOpt():Option[Element] = Option(self.first())
  }

  implicit def Elements2Collection(self:Elements) = self.asScala

  object Tag {
    def unapply(n:org.jsoup.nodes.Node): Option[String] = {
      n match {
        case e:Element => Some(e.tagName.toLowerCase)
        case _ => None
      }
    }
  }
}

object HtmlParser {
  import java.io.File
  import java.nio.charset.{Charset, StandardCharsets}
  import org.jsoup.Jsoup
  import org.jsoup.nodes.{Document, Element}

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
    val fullName = s"${ns}.${name}"
    val kind:String = doc / "#signature > .modifier_kind > .kind" first() text()
    val comment = extractMarkup(doc / "#comment" first())
    val entity =
      kind match {
        case "trait" | "class" =>
          extractType(Id.Type(fullName), TypeKind.forName(kind), comment, doc)
        case "object" => Object(Id.Value(s"${ns}.${name}$$"), comment)
        case unk => errorUnknown("TypeKind", unk)
      }
    entity +: extractValueMembers(entity.id, doc)
  }

  def extractNS(doc:Document):String = {
    doc / "#definition #owner a.extype" last() attr("name")
  }

  def extractType(id:Id.Type, kind:TypeKind, comment:Seq[Markup], doc:Document):Type = {
    val typeParams = extractTypeParams(doc / "#signature > .symbol > .tparams" first())
    new Type(id, kind, typeParams, comment)
  }

  def extractTypeParams(elm:Element):TypeParams = {
    TypeParams(elm.text())
  }

  def extractValueMembers(parentId:Id, doc:Document):Seq[Item] = {
    (doc / "#values > ol > li").map {elm =>
      val id = Id.Value(elm.attr("name"))
      val kind = ValueKind.forName(
        elm / ".signature > .modifier_kind > .kind" first() text())
      val comment =
        elm / ".fullcomment" firstOpt() map(extractMarkup(_)) match {
          case Some(a) => a
          case None => elm / ".fullcomment" firstOpt() map(extractMarkup(_)) getOrElse Seq()
        }
      kind match {
        case ValueKind.Def | ValueKind.Val | ValueKind.Var =>
          val params = MethodParams(elm / ".signature > .symbol > .params" text())
          val resultType = ResultType(elm / ".signature > .symbol > .result" text() replaceAll("^: ", ""))
          if(parentId.isParentOf(id))
            DefinedMethod(id, params, resultType, comment)
          else if((elm / ".signature > .symbol > .implicit").nonEmpty)
            ViaImplicitMethod(id.changeParent(parentId), params, resultType, id, comment)
          else
            ViaInheritMethod(id.changeParent(parentId), params, resultType, id, comment)
        case ValueKind.Object => Object(id, comment)
      }
    }
  }

  def extractMarkup(elm:org.jsoup.nodes.Node):Seq[Markup] = {
    import Markup._
    import org.jsoup.{nodes => n}
    elm.childNodes.asScala.collect {
      case c:n.TextNode => Seq(Text(c.text()))
      case c:n.Element if(c.tagName.toLowerCase == "p") =>
        Seq(Paragraph(extractMarkup(c)))
      case c:n.Element if(c.tagName.toLowerCase == "dl") =>
        Seq(extractDlMarkup(c))
      case e:Element => // Treat as text if unknown element
        Seq(Text(e.text()))
    }.flatten
  }
  def extractDlMarkup(dl:Element):Markup = {
    var dtPrev:org.jsoup.nodes.Node = null
    val items = new scala.collection.mutable.ArrayBuffer[Markup.DlItem]
    dl.childNodes.asScala.foreach {
      case dt@Tag("dt") =>
        dtPrev = dt
      case dd@Tag("dd") =>
        if(dtPrev != null) {
          items += Markup.DlItem(extractMarkup(dtPrev), extractMarkup(dd))
          dtPrev = null
        }
      case _ =>
    }
    return Markup.Dl(items)
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
    appendln(item.signature)
    item.comment.map(formatMarkup(_)).foreach(appendln(_))

    repo.childrenOf(item).foreach {child =>
      assert(item.id.isParentOf(child.id))

      sb.append(" - ")
      appendln(child.signature)
      child.comment.map(formatMarkup(_)).foreach(appendln(_))

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

  def formatMarkup(markups:Seq[Markup]):String =
    markups.map(formatMarkup(_)).mkString("")
  def formatMarkup(markup:Markup):String = markup match {
    case Markup.Text(content) => content
    case Markup.Paragraph(children) =>
      children.map(formatMarkup(_)).mkString("\n\n")
    case Markup.Dl(items) =>
      items.map {item =>
        formatMarkup(item.dt) + "\n" + "    " + formatMarkup(item.dd)
      } mkString("\n")
  }
}
