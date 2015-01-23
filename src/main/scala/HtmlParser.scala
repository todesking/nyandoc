package com.todesking.nyandoc

object HtmlParser {
  import java.io.File
  import java.nio.charset.{Charset, StandardCharsets}
  import org.jsoup.Jsoup

  // members: Seq[(categoryName:String, Item)]
  case class Result(topItem:Item, members:Seq[(String, Item)])

  def parse(file:File, charset:Charset = StandardCharsets.UTF_8):Option[Result] = {
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
    parse(sb.toString)
  }

  def parse(content:String):Option[Result] = {
    val baseUri = ""
    val doc = Jsoup.parse(content, baseUri)
    ScaladocHtmlParser.parse(doc) orElse JavadocHtmlParser17.parse(doc) orElse JavadocHtmlParser16.parse(doc)
  }
}

object ScaladocHtmlParser {
  import org.jsoup.Jsoup
  import org.jsoup.nodes.{Document, Element, Node}

  import LibGlobal._
  import JsoupExt._
  import scala.collection.JavaConverters._

  val languageName = "scala"

  def parse(doc:Document):Option[HtmlParser.Result] = {
    for {
      top <- extractToplevelItem(doc)
      members = extractMembers(top.id, doc)
    } yield HtmlParser.Result(top, members)
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
          Type(tid, languageName, TypeKind.forName(kind), signature, comment)
        case vid:Id.Value =>
          kind match {
            case "object" =>
              Object(vid, languageName, signature, comment)
            case "package" =>
              Package(vid, languageName, signature, comment)
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
    for {
      localNameE <- doc / "#definition > h1" firstOpt()
      localName = localNameE cleanText()
      signatureE <- doc / "#signature > .modifier_kind .kind" firstOpt()
      signature = signatureE cleanText()
    } yield {
      buildId(parentId, localName, signature)
    }
  }

  def extractMembers(topId:Id, doc:Document):Seq[(String, Item)] = {
    doc / "#allMembers > div" flatMap {catElm =>
      val categoryName = catElm / "h3" firstOrDie() cleanText()
      if(categoryName == "Shadowed Implicit Value Members")
        Seq()
      else
        catElm / "> ol > li" map {itemElm =>
          (categoryName, extractMemberItem(topId, itemElm))
        }
    }
  }

  def extractMemberItem(topId:Id, elm:Element):Item = {
    val id = extractMemberId(topId, elm)
    val kind = elm / ".signature > .modifier_kind > .kind" firstOrDie() cleanText()
    val comment =
      elm / "> .fullcomment" firstOpt() map(extractMarkup(_)) orElse {
        elm / ".shortcomment" firstOpt() map(extractMarkup(_))
      } getOrElse Seq()

    val signature = elm / ".signature" lastOrDie() cleanText()

    id match {
      case tid:Id.Type =>
        Type(tid, languageName, TypeKind.forName(kind), signature, comment)
      case vid:Id.Value =>
        kind match {
          case "def" | "val" | "var" | "lazy val" | "new" =>
            val params = MethodParams("TODO: remove this")
            val resultType = ResultType("TODO: remove this")

            val subId = elm / "> a:eq(1)" attr("id") replaceAll("""\.""", "/")
            val fullId = Id.ChildValue(vid.asInstanceOf[Id.Child].parentId, subId)

            val permaLink = elm / "> .permalink > a" firstOpt() map(_.attr("href"))

            if(permaLink.isEmpty)
              DefinedMethod(fullId, languageName, params, resultType, signature, comment)
            else {
              val realId = elm.attr("name").replaceAll("""#[^#.]+$""", "")
              if((elm / ".signature > .symbol > .implicit").nonEmpty)
                ViaImplicitMethod(fullId, languageName, params, resultType, signature, realId, comment)
              else
                ViaInheritMethod(fullId, languageName, params, resultType, signature, realId, comment)
            }
            case "object" =>
              Object(vid, languageName, signature, comment)
            case "package" =>
              Package(vid, languageName, signature, comment)
        }
    }
  }

  def extractMemberId(topId:Id, elm:Element):Id = {
    val localName = elm / ".signature > .symbol .name" firstOpt() getOrElse {
      elm / ".signature > .symbol .implicit" firstOrDie()
    } cleanText()
    val kind = elm / ".signature > .modifier_kind > .kind" firstOrDie() cleanText()
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

  def extractMarkup(elm:Node):Seq[Markup] =
    HtmlToMarkup.extract(elm, "scala")
}

object JavadocHtmlParser17 {
  import org.jsoup.Jsoup
  import org.jsoup.nodes.{Document, Element, Node, TextNode}

  import LibGlobal._
  import JsoupExt._
  import scala.collection.JavaConverters._

  val languageName = "java"

  def parse(doc:Document):Option[HtmlParser.Result] = {
    for {
      top <- extractToplevelItem(doc)
      members = extractMembers(top.id, doc)
    } yield HtmlParser.Result(top, members)
  }

  def extractToplevelItem(doc:Document):Option[Item] = {
    for {
     _ <- doc / ".details" firstOpt()
     // compact classes has two subtitle: First one like "compact1, compact2, compact3"
     // Second subtitle is namespace.
     ns <- doc / ".header > .subtitle" lastOpt() map(_.cleanText().split("""\."""))
     sig <- doc / ".header > .title" firstOpt() map(_.cleanText())
    } yield {
      val nsId = ns.foldLeft[Id](Id.Root) {(parent, name) => Id.ChildValue(parent, name)}
      val sigR = """^(Class|Interface|Enum|Annotation Type)\s+([a-zA-Z0-9_]+(?:\.[a-zA-Z0-9_]+)*)(<.*>)?$""".r

      sig match {
        case sigR(kind, name, targs) =>
          val id = Id.ChildType(nsId, name)
          // TODO: It may need more specialized version for javadoc.
          val comment =
            doc / ".contentContainer > .description > .blockList > .blockList > .block" firstOpt() map(extractMarkup(_)) getOrElse Seq()
          val detailedSig = doc / ".contentContainer > .description > .blockList > .blockList > pre" firstOrDie() cleanText()
          // TODO: restructure TypeKind
          Type(id, languageName, TypeKind.Trait, detailedSig, comment)
        case _ =>
          errorUnknown("Javadoc signature", sig)
      }
    }
  }

  def extractMembers(topId:Id, doc:Document):Seq[(String, Item)] = {
    doc / ".details > ul.blocklist > li.blockList > ul.blocklist" flatMap {group =>
      val categoryName = group / "> li > h3" firstOpt() map(_.cleanText()) getOrElse "(default category)"
      val members = scala.collection.mutable.ArrayBuffer.empty[(String, Item)]
      var curName:String = null
      (group / "> li.blockList").firstOrDie().childNodes().asScala.foreach {
        case Tag("a", a) =>
          curName = a.attr("name")
        case Tag("ul", ul) if(curName != null)=>
          // first 2 elements is header(method name and signature)
          val comment = ul / "> li > *:gt(1)" flatMap(extractMarkup(_))
          val id = Id.ChildValue(topId, curName)
          val signature = ul / "> li > pre" firstOrDie() cleanText()
          members += categoryName -> DefinedMethod(id, languageName, MethodParams(""), ResultType(""), signature, comment)
          curName = null
        case Tag("h3", _) =>
          // ignore
        case _:TextNode =>
          // ignore
        case tag =>
          unsupportedFeature("member list DOM", tag.toString)
      }
      members
    }
  }

  def extractMarkup(elm:Node):Seq[Markup] =
    HtmlToMarkup.extract(elm, languageName)
}

object JavadocHtmlParser16 {
  import org.jsoup.Jsoup
  import org.jsoup.nodes.{Document, Element, Node, TextNode}

  import LibGlobal._
  import JsoupExt._
  import scala.collection.JavaConverters._

  val languageName = "java"

  def parse(doc:Document):Option[HtmlParser.Result] = {
    for {
      top <- extractToplevelItem(doc)
      members = extractMembers(top.id, doc)
    } yield HtmlParser.Result(top, members)
  }

  def extractToplevelItem(doc:Document):Option[Item] = {
    for {
     ns <- doc / "body > h2 > font" firstOpt() map(_.cleanText().split("""\."""))
     sig <- doc / "body > h2" firstOpt() map { h2 => h2.childNodes.asScala.collect { case t: TextNode =>  t.cleanText() } mkString("") }
    } yield {
      val nsId = ns.foldLeft[Id](Id.Root) {(parent, name) => Id.ChildValue(parent, name)}
      val sigR = """^(Class|Interface|Enum|Annotation Type)\s+([a-zA-Z0-9_]+(?:\.[a-zA-Z0-9_]+)*)(<.*>)?$""".r

      sig match {
        case sigR(kind, name, targs) =>
          val id = Id.ChildType(nsId, name)
          val comment = extractToplevelComment(doc)
          val detailedSig = doc / "h2 ~ dl" / "dt > pre" firstOrDie() cleanText()
          // TODO: restructure TypeKind
          Type(id, languageName, TypeKind.Trait, detailedSig, comment)
        case _ =>
          errorUnknown("Javadoc signature", sig)
      }
    }
  }

  def extractToplevelComment(doc: Document): Seq[Markup] = {
    var result = new scala.collection.mutable.ArrayBuffer[Markup]
    var node: Node = doc / "h2 ~ hr ~ dl ~ p" firstOrDie()

    while(node != null) {
      result += Markup.Paragraph(extractMarkup(node))
      node = node.nextSibling()
    }
    Markup.normalize(result)
  }

  def extractMembers(topId:Id, doc:Document):Seq[(String, Item)] = {
    doc / ".details > ul.blocklist > li.blockList > ul.blocklist" flatMap {group =>
      val categoryName = group / "> li > h3" firstOpt() map(_.cleanText()) getOrElse "(default category)"
      val members = scala.collection.mutable.ArrayBuffer.empty[(String, Item)]
      var curName:String = null
      (group / "> li.blockList").firstOrDie().childNodes().asScala.foreach {
        case Tag("a", a) =>
          curName = a.attr("name")
        case Tag("ul", ul) if(curName != null)=>
          // first 2 elements is header(method name and signature)
          val comment = ul / "> li > *:gt(1)" flatMap(extractMarkup(_))
          val id = Id.ChildValue(topId, curName)
          val signature = ul / "> li > pre" firstOrDie() cleanText()
          members += categoryName -> DefinedMethod(id, languageName, MethodParams(""), ResultType(""), signature, comment)
          curName = null
        case Tag("h3", _) =>
          // ignore
        case _:TextNode =>
          // ignore
        case tag =>
          unsupportedFeature("member list DOM", tag.toString)
      }
      members
    }
  }

  def extractMarkup(elm:Node):Seq[Markup] =
    HtmlToMarkup.extract(elm, languageName)
}

object HtmlToMarkup {
  def extract(elm:org.jsoup.nodes.Node, codeLanguage:String):Seq[Markup] =
    new HtmlToMarkup(codeLanguage).extract(elm)
}

class HtmlToMarkup(codeLanguage:String) {
  import org.jsoup.nodes.{Document, Element, Node}

  import JsoupExt._
  import LibGlobal._
  import scala.collection.JavaConverters._

  def extract(elm:Node):Seq[Markup] =
    Markup.normalize(extract0(elm))

  def extract0(elm:Node):Seq[Markup] = {
    import Markup._
    import org.jsoup.{nodes => n}

    elm match {
      case Tag("dl", e) =>
        return extractDlMarkup(e)
      case _ =>
    }

    elm.childNodes.asScala.collect {
      case c:n.TextNode => Seq(Text(c.cleanText()))
      case Tag("p", e) =>
        Seq(Paragraph(extract(e)))
      case Tag("dl", e) =>
        extractDlMarkup(e)
      case Tag("a", e) =>
        val url = e.attr("href")
        if(url.isEmpty) {
          extract(e)
        } else if(url.startsWith("http:") || url.startsWith("https:") || url.startsWith("//")) {
          Seq(LinkExternal(e.text(), url))
        } else {
          Seq(LinkInternal(e.text(), url))
        }
      case Tag("span", e) =>
        extract0(e)
      case Tag("br", e) =>
        Seq(Text("\n"))
      case Tag("div", e) =>
        if(e.hasClass("toggleContainer"))
          Seq()
        else
          extract0(e)
      case Tag("pre", e) =>
        Seq(Code(e.text(), codeLanguage))
      case Tag("code" | "tt", e) =>
        Seq(CodeInline(e.text()))
      case Tag("b" | "em" | "strong", e) =>
        Seq(Bold(extract(e)))
      case Tag("i", e) =>
        Seq(Italic(extract(e)))
      case Tag("ol", e) if((e / "> li.cmt").size > 0) => // code example in scaladoc
        e / "> li.cmt > p > code" firstOpt() map {oneline =>
          Seq(Code(oneline.text(), codeLanguage))
        } getOrElse {
          Seq(Code(e / "> li.cmt > pre" text(), codeLanguage))
        }
      case Tag("ul", e) =>
        Seq(UnorderedList(e / "> li" map {li =>ListItem(extract(li))}))
      case Tag("ol", e) =>
        Seq(OrderedList(e / "> li" map {li => ListItem(extract(li))}))
      case Tag("h2" | "h3" | "h4" | "h5" | "h6", e) =>
        Seq(Heading(extract(e)))
      case Tag("font", e) =>
        // Often in javadoc
        if(e.attr("size") == "-2" && e.text() == "TM")
          Seq(Text("(TM)"))
        else
          extract(e)
      case Tag("sup", e) =>
        if(e.text() == "TM")
          Seq(Text("(TM)"))
        else
          Seq(Sup(extract(e)))
      case Tag("hr", _) =>
        Seq(HorizontalLine())
      case Tag("blockquote", e) =>
        // Sometimes in javadoc, blockquote is used for container of "code example" or something
        val inner = extract(e)
        inner match {
          case code@Seq(Code(_, _)) =>
            code
          case _ =>
            Seq(BlockQuote(extract(e)))
        }
      case Tag("table", e) =>
        // FIXME: VERY LOW QUALITY RESULT.
        Seq(Table(e / "tr,th" map {tr => TableRow(tr / "> td" map {td => TableColumn(extract(td))}) }))
      case e:Element => // Treat as text if unknown element
        unsupportedFeature("markup tag", e.toString)
        Seq(Text(e.cleanText()))
    }.flatten
  }

  def extractDlMarkup(dl:Element):Seq[Markup] = {
    import org.jsoup.{nodes => n}

    // sometimes insane markup was found. Like <dl> <b> <dt>...</dt> <dd>...</dd> </b> </dl>
    if((dl / "> b").size > 0)
      return extractDlMarkup(dl / "> b" firstOrDie())

    var dtPrev:org.jsoup.nodes.Node = null
    val items = new scala.collection.mutable.ArrayBuffer[Markup.DlItem]
    val others = new scala.collection.mutable.ArrayBuffer[Markup]
    dl.childNodes.asScala.foreach {
      case Tag("dt", dt) =>
        dtPrev = dt
      case Tag("dd", dd) =>
        if(dtPrev != null) {
          items += Markup.DlItem(extract(dtPrev), extract(dd))
          dtPrev = null
        }
      case Tag("div", div) if(div.hasClass("full-signature-block")) =>
        // junk
      case Tag("div", div) if(div.hasClass("block")) =>
        // code example
        others ++= extract0(div)
      case t:n.TextNode =>
        // just ignore
      case other =>
        unsupportedFeature("markup(dl)", other.toString)
    }
    return Markup.Dl(items) +: others
  }
}
