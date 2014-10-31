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
    for {
      top <- extractToplevelItem(doc)
      members = extractMembers(top.id, doc)
    } yield (top, members)
  }

  def extractToplevelItem(doc:Document):Option[Item] = {
    for {
      id <- extractTopLevelId(doc)
    } yield {
      val kind:String = doc / "#signature > .modifier_kind > .kind" first() cleanText()
      val signature = doc / "#signature" first() cleanText()
      val comment = doc / "#comment" firstOpt() map(extractMarkup(_)) getOrElse Seq()
      id match {
        case tid:Id.Type =>
          Type(tid, TypeKind.forName(kind), signature, comment)
        case vid:Id.Value =>
          kind match {
            case "object" =>
              Object(vid, signature, comment)
            case "package" =>
              Package(vid, signature, comment)
            case unk => errorUnknown("Value member kind", unk)
          }
      }
    }
  }

  def extractTopLevelId(doc:Document):Option[Id] = {
    val isPackage = """.*(?:/|\A)package\.html\z""".r
    val isObject = """.*\$\.html$""".r
    val parentId =
      (doc / "#owner > a").foldLeft[Id](Id.Root) {(parent, elm) =>
        val name = elm.cleanText()
        elm.attr("href") match {
          case isPackage() | isObject() =>
            Id.ChildValue(parent, elm.cleanText())
          case _ =>
            Id.ChildType(parent, elm.cleanText())
        }
      }
    println(parentId)
    for {
      localNameE <- doc / "#definition > h1" firstOpt()
      localName = localNameE cleanText()
      signatureE <- doc / "#signature > .modifier_kind .kind" firstOpt()
      signature = signatureE cleanText()
    } yield {
      buildId(parentId, localName, signature)
    }
  }

  def extractMembers(topId:Id, doc:Document):Seq[Item] = {
    doc / "#allMembers > div" flatMap {catElm =>
      val categoryName = catElm / "> h3" first() cleanText()
      if(categoryName == "Shadowed Implicit Value Members")
        Seq()
      else
        catElm / "> ol > li" map {itemElm =>
          extractMemberItem(topId, itemElm)
        }
    }
  }

  def extractMemberItem(topId:Id, elm:Element):Item = {
    val id = extractMemberId(topId, elm)
    val kind = elm / "> .signature > .modifier_kind > .kind" firstOrDie() cleanText()
    val comment =
      elm / "> .fullcomment" firstOpt() map(extractMarkup(_)) orElse {
        elm / ".shortcomment" firstOpt() map(extractMarkup(_))
      } getOrElse Seq()

    val signature = elm / ".signature" lastOrDie() cleanText()

    id match {
      case tid:Id.Type =>
        Type(tid, TypeKind.forName(kind), signature, comment)
      case vid:Id.Value =>
        kind match {
          case "def" | "val" | "var" | "lazy val" | "new" =>
            val params = MethodParams("TODO: remove this")
            val resultType = ResultType("TODO: remove this")

            val subId = elm / "> a:eq(1)" attr("id") replaceAll("""\.""", "/")
            val fullId = Id.ChildValue(vid.asInstanceOf[Id.Child].parentId, subId)

            val permaLink = elm / "> .permalink > a" firstOpt() map(_.attr("href"))

            if(permaLink.isEmpty)
              DefinedMethod(fullId, params, resultType, signature, comment)
            else {
              val realId = Id.Value("__dummy__(not_implemented_yet)")
              if((elm / ".signature > .symbol > .implicit").nonEmpty)
                ViaImplicitMethod(fullId, params, resultType, signature, realId, comment)
              else
                ViaInheritMethod(fullId, params, resultType, signature, realId, comment)
            }
            case "object" =>
              Object(vid, signature, comment)
            case "package" =>
              Package(vid, signature, comment)
        }
    }
  }

  def extractMemberId(topId:Id, elm:Element):Id = {
    val localName = elm / ".signature > .symbol .name" firstOpt() getOrElse {
      elm / ".signature > .symbol .implicit" firstOrDie()
    } cleanText()
    val kind = elm / "> .signature > .modifier_kind > .kind" firstOrDie() cleanText()
    buildId(topId, localName, kind)
  }

  def buildId(parentId:Id, localName:String, kind:String):Id =
      kind match {
        case "type" | "class" | "case class" | "trait" =>
          Id.ChildType(parentId, localName)
        case "object" | "def" | "var" | "val" | "lazy val" | "new" | "package" =>
          Id.ChildValue(parentId, localName)
        case _ =>
          throw errorUnknown("item kind", kind)
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
