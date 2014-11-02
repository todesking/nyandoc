package com.todesking.dox

object JsoupExt {
  import org.jsoup.select.Elements
  import org.jsoup.nodes.{Element, Node}
  import org.jsoup.nodes
  import scala.collection.JavaConverters._

  import scala.language.implicitConversions

  object TextCleaner {
    def clean(s:String):String =
      """\s+""".r.replaceAllIn(s, " ").replaceAll("""^\s+|\s+$""", "")
  }

  implicit class ElementExt[A <: Element](self:A) {
    def /(query:String) = self.select(query)
    def cleanText():String =
      TextCleaner.clean(self.text())
  }

  implicit class TextNodeExt[A <: nodes.TextNode](self:A) {
    def cleanText():String =
      TextCleaner.clean(self.text())
  }

  implicit class ElementsExt[A <: Elements](self:A) {
    def /(query:String) = self.select(query)
    def firstOpt():Option[Element] = Option(self.first())
    def lastOpt():Option[Element] = Option(self.last())
    def firstOrDie():Element = firstOpt() getOrElse { throw new RuntimeException("element not found") }
    def lastOrDie():Element = lastOpt() getOrElse { throw new RuntimeException("element not found") }
  }

  implicit def Elements2Collection(self:Elements) = self.asScala

  object Tag {
    val lowerCache = scala.collection.mutable.HashMap.empty[String, String]

    def toLowerCase(s:String) = synchronized {
      lowerCache.get(s) getOrElse {
        val lowercase = s.toLowerCase
        lowerCache += (s -> lowercase)
        lowercase
      }
    }
    def unapply(n:Node): Option[(String, Element)] = {
      n match {
        case e:Element => Some(toLowerCase(e.tagName), e)
        case _ => None
      }
    }
  }
}
