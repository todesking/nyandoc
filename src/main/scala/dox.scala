package com.todesking.dox

object LibGlobal {
  def errorUnknown(name:String, unk:String) =
    throw new RuntimeException(s"Unknown ${name}: unk")
}
import LibGlobal._

case class Identifier(fullName:String)

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

sealed class Item(val id:Identifier)

sealed class Value(id:Identifier/*, tpe:TypeInstance*/) extends Item(id)
case class Type(override val id:Identifier) extends Item(id)

case class Object(override val id:Identifier) extends Value(id) with ValueContainer
case class Method(override val id:Identifier) extends Value(id)


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

class HtmlParser {
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
      val kind = ValueKind.fromName(elm / ".signature .modifier_kind .kind" text())
      kind match {
        case ValueKind.Def | ValueKind.Val | ValueKind.Var => Method(id)
        case ValueKind.Object => Object(id)
      }
    }
  }
}
