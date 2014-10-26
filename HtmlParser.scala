package com.todesking.dox


object HtmlParser {
  import java.io.File
  import java.nio.charset.{Charset, StandardCharsets}
  import org.jsoup.Jsoup
  import org.jsoup.nodes.{Document, Element}

  import LibGlobal._
  import JsoupExt._
  import scala.collection.JavaConverters._

  def parse(file:File, charset:Charset = StandardCharsets.UTF_8):Seq[Item] = {
    import java.io._
    val sb = new StringBuilder
    val reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset))
    try {
      var line = reader.readLine()
      while(line != null) {
        sb.append(line)
        line = reader.readLine()
      }
    } finally {
      reader.close()
    }
    val html = sb.toString
    val baseUri = ""
    parse(Jsoup.parse(html, baseUri))
  }

  def parse(doc:Document):Seq[Item] = {
    // TODO: Type params
    // TODO: Linear supertypes
    // TODO: Known subclasses
    val name:String = doc / "#definition > h1" text()
    if(name == "")
      return Seq()
    val ns:String = extractNS(doc)
    val fullName = s"${ns}.${name}"
    val kind:String = doc / "#signature > .modifier_kind > .kind" firstOpt() map(_.text()) getOrElse "kind_not_found"
    val comment = doc / "#comment" firstOpt() map(extractMarkup(_)) getOrElse Seq()
    val entity =
      kind match {
        case "trait" | "class" | "case class" | "type" =>
          extractType(Id.Type(fullName), TypeKind.forName(kind), comment, doc)
        case "object" | "package" =>
          Object(Id.Value(s"${ns}.${name}$$"), comment)
        case unk => errorUnknown("Item kind", unk)
      }
    entity +: extractValueMembers(entity.id, doc)
  }

  def extractNS(doc:Document):String = {
    doc / "#definition #owner a.extype" lastOpt() map(_.attr("name")) getOrElse ""
  }

  def extractType(id:Id.Type, kind:TypeKind, comment:Seq[Markup], doc:Document):Type = {
    val typeParams = doc / "#signature > .symbol > .tparams" firstOpt() map(extractTypeParams(_)) getOrElse TypeParams("")
    new Type(id, kind, typeParams, comment)
  }

  def extractTypeParams(elm:Element):TypeParams = {
    TypeParams(elm.text())
  }

  def extractValueMembers(parentId:Id, doc:Document):Seq[Item] = {
    (doc / "#values > ol > li").map {elm =>
      val id = Id.Value(elm.attr("name"))
      val kind = ValueKind.forName(
        elm / "> .signature > .modifier_kind > .kind" firstOpt() map(_.text()) getOrElse "kind not found")
      val comment =
        elm / "> .fullcomment" firstOpt() map(extractMarkup(_)) match {
          case Some(a) => a
          case None => elm / ".shortcomment" firstOpt() map(extractMarkup(_)) getOrElse Seq()
        }
      kind match {
        case ValueKind.Def | ValueKind.Val | ValueKind.Var =>
          val params = MethodParams(elm / ".signature > .symbol > .params" text())
          val resultType = ResultType(elm / ".signature > .symbol > .result" text() replaceAll("^: ", ""))

          def signature = elm / ".signature" last() text()
          if(parentId.isParentOf(id))
            DefinedMethod(id, params, resultType, signature, comment)
          else if((elm / ".signature > .symbol > .implicit").nonEmpty)
            ViaImplicitMethod(id.changeParent(parentId), params, resultType, signature, id, comment)
          else
            ViaInheritMethod(id.changeParent(parentId), params, resultType, signature, id, comment)
        case ValueKind.Object | ValueKind.Package => Object(id, comment)
      }
    }
  }

  def extractMarkup(elm:org.jsoup.nodes.Node):Seq[Markup] =
    optimizeMarkups(extractMarkup0(elm))

  def optimizeMarkups(markups:Seq[Markup]):Seq[Markup] =
    if(markups.size < 2) markups
    else {
      val tail = optimizeMarkups(markups.tail)
      (markups.head, tail.head) match {
        case (Markup.Text(c1), Markup.Text(c2)) =>
          Markup.Text(c1 + " " + c2) +: tail.tail
        case _ => markups.head +: tail
      }
    }

  def extractMarkup0(elm:org.jsoup.nodes.Node):Seq[Markup] = {
    import Markup._
    import org.jsoup.{nodes => n}
    elm.childNodes.asScala.collect {
      case c:n.TextNode => Seq(Text(c.cleanText()))
      case Tag("p", e) =>
        Seq(Paragraph(extractMarkup(e)))
      case Tag("dl", e) =>
        extractDlMarkup(e)
      case Tag("a", e) =>
        // TODO: support LinkInternal
        Seq(LinkExternal(e.text(), e.attr("href")))
      case Tag("span", e) =>
        extractMarkup(e)
      case Tag("br", e) =>
        Seq(Text("\n"))
      case Tag("div", e) =>
        if(e.hasClass("toggleContainer"))
          Seq()
        else
          extractMarkup(e)
      case Tag("pre", e) =>
        Seq(Code(e.text()))
      case Tag("code", e) =>
        Seq(CodeInline(e.text()))
      case e:Element => // Treat as text if unknown element
        unsupportedFeature("markup tag", e.tagName)
        Seq(Text(e.cleanText()))
    }.flatten
  }

  def extractDlMarkup(dl:org.jsoup.nodes.Node):Seq[Markup] = {
    import org.jsoup.{nodes => n}

    var dtPrev:org.jsoup.nodes.Node = null
    val items = new scala.collection.mutable.ArrayBuffer[Markup.DlItem]
    val others = new scala.collection.mutable.ArrayBuffer[Markup]
    dl.childNodes.asScala.foreach {
      case Tag("dt", dt) =>
        dtPrev = dt
      case Tag("dd", dd) =>
        if(dtPrev != null) {
          items += Markup.DlItem(extractMarkup(dtPrev), extractMarkup(dd))
          dtPrev = null
        }
      case Tag("div", div) if(div.hasClass("full-signature-block")) =>
        // junk
      case Tag("div", div) if(div.hasClass("block")) =>
        // code example
        others ++= extractMarkup(div)
      case t:n.TextNode =>
      case other =>
        unsupportedFeature("markup(dl)", other.toString)
    }
    return Markup.Dl(items) +: others
  }
}
