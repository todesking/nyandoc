package com.todesking.dox

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
    def lastOpt():Option[Element] = Option(self.last())
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
    def unapply(n:org.jsoup.nodes.Node): Option[String] = {
      n match {
        case e:Element => Some(toLowerCase(e.tagName))
        case _ => None
      }
    }
  }
}
