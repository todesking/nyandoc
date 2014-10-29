package com.todesking.dox


object HtmlParser {
  import java.io.File
  import java.nio.charset.{Charset, StandardCharsets}
  import org.jsoup.Jsoup
  import org.jsoup.nodes.{Document, Element}

  import LibGlobal._
  import JsoupExt._
  import scala.collection.JavaConverters._

  def parse(file:File, charset:Charset = StandardCharsets.UTF_8):Option[(Item, Seq[Item])] = {
    import java.io._
    val sb = new StringBuilder
    val reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset))
    try {
      var line = reader.readLine()
      while(line != null) {
        sb.append(line)
        sb.append('\n')
        line = reader.readLine()
      }
    } finally {
      reader.close()
    }
    val html = sb.toString
    val baseUri = ""
    parse(Jsoup.parse(html, baseUri))
  }

  def parse(doc:Document):Option[(Item, Seq[Item])] = {
    val fullName = (for {
        script <- (doc / "head > script").filter(_.data().contains("var hash =")).headOption
        data = script.data()
        m <- """(?m).*var hash = '(.+)'.*""".r.findFirstMatchIn(data)
      } yield m.group(1)
    ) getOrElse ""
    println(fullName)
    if(fullName isEmpty()) {
      println("  => Skipped")
      return None
    }
    val kind:String = doc / "#signature > .modifier_kind > .kind" first() cleanText()
    val signature = doc / "#signature" first() cleanText()
    val comment = doc / "#comment" firstOpt() map(extractMarkup(_)) getOrElse Seq()
    val entity =
      kind match {
        case "trait" | "class" | "case class" | "type" =>
          Type(Id.Type(fullName), TypeKind.forName(kind), signature, comment)
        case "object" =>
          Object(Id.Value(fullName), signature, comment)
        case "package" =>
          Package(Id.Value(fullName), comment)
        case unk => errorUnknown("Item kind", unk)
      }
    println(s"  => $fullName")
    Some(entity, extractValueMembers(entity.id, doc))
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
      def signature = elm / ".signature" last() text()
      kind match {
        case ValueKind.Def | ValueKind.Val | ValueKind.Var =>
          val params = MethodParams(elm / ".signature > .symbol > .params" text())
          val resultType = ResultType(elm / ".signature > .symbol > .result" text() replaceAll("^: ", ""))
          val subId = elm / "> a:eq(1)" attr("id") replaceAll("""\.""", "/")
          val fullId = Id.ChildValue(id.parentId, subId)

          def signature = elm / ".signature" last() text()
          if(parentId.isParentOf(fullId))
            DefinedMethod(fullId, params, resultType, signature, comment)
          else if((elm / ".signature > .symbol > .implicit").nonEmpty)
            ViaImplicitMethod(fullId.changeParent(parentId), params, resultType, signature, fullId, comment)
          else
            ViaInheritMethod(fullId.changeParent(parentId), params, resultType, signature, fullId, comment)
        case ValueKind.Object =>
          val newId = Id.Value(id.fullName + "$") // :(
          Object(newId, signature, comment)
        case ValueKind.Package =>
          Package(id, comment)
      }
    }
  }

  def extractMarkup(elm:org.jsoup.nodes.Node):Seq[Markup] =
    Markup.normalize(extractMarkup0(elm))

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
        val url = e.attr("href")
        if(url.startsWith("http:") || url.startsWith("https:") || url.startsWith("//")) {
          Seq(LinkExternal(e.text(), url))
        } else {
          Seq(LinkInternal(e.text(), url))
        }
      case Tag("span", e) =>
        extractMarkup0(e)
      case Tag("br", e) =>
        Seq(Text("\n"))
      case Tag("div", e) =>
        if(e.hasClass("toggleContainer"))
          Seq()
        else
          extractMarkup0(e)
      case Tag("pre", e) =>
        Seq(Code(e.text()))
      case Tag("code", e) =>
        Seq(CodeInline(e.text()))
      case Tag("b", e) =>
        Seq(Bold(extractMarkup(e)))
      case Tag("i", e) =>
        Seq(Italic(extractMarkup(e)))
      case Tag("ol", e) if((e / "> li.cmt").size > 0) => // code example
        e / "> li.cmt > p > code" firstOpt() map {oneline =>
          Seq(Code(oneline.text()))
        } getOrElse {
          Seq(Code(e / "> li.cmt > pre" text()))
        }
      case Tag("ul", e) =>
        Seq(UnorderedList(e / "> li" map {li =>ListItem(extractMarkup(li))}))
      case e:Element => // Treat as text if unknown element
        unsupportedFeature("markup tag", e.toString)
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
        others ++= extractMarkup0(div)
      case t:n.TextNode =>
      case other =>
        unsupportedFeature("markup(dl)", other.toString)
    }
    return Markup.Dl(items) +: others
  }
}
