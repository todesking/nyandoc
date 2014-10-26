package com.todesking.dox

object Markdown {
  def code(str:String):String = s"""
    |```scala
    |${str}
    |```
    |
    |""".stripMargin

  def inlineCode(str:String):String = s"`${cleanup(str)}`"

  def h(level:Int)(str:String):String = ("#" * level) + " " + str + "\n\n"

  def paragraph(str:String):String =
    cleanup(str) + "\n\n"

  def link(title:String, url:String):String =
    s"[${title}](${url})"

  // Seq[(dtContent, ddContent)]
  def definitionList(items:Seq[(String ,String)]):String =
    (items.map {case (dt, dd) =>
      "* " + cleanup(dt) + "\n" + "    * " + cleanup(dd) + "\n"
    }).mkString("") + "\n"

  def bold(content:String) =
    s" *${content}* "

  def italic(content:String) =
    s" _${content}_ "

  private def cleanup(str:String):String =
    """^\n+|\n+$""".r.replaceAllIn(str, "")
}

class MarkdownFormatter {
  def format(item:Item, repo:Repository):String = {
    val sb = new scala.collection.mutable.StringBuilder
    import Markdown._

    sb.append(h(1)(item.id.fullName))
    sb.append(code(item.signature))
    item.comment.map(formatMarkup(_)).foreach(sb.append(_))

    repo.childrenOf(item).foreach {child =>
      assert(item.id.isParentOf(child.id))

      sb.append(h(2)(inlineCode(child.signature)))
      child.comment.map(formatMarkup(_)).foreach(sb.append(_))

      child match {
        case c:ViaImplicitMethod =>
          sb.append(s" (added by implicit convertion: ${c.originalId})\n")
        case c:ViaInheritMethod =>
          sb.append(s"    (defined at ${c.originalId})\n")
        case _ =>
      }
      sb.append("\n")
    }

    sb.toString
  }

  def formatMarkup(markups:Seq[Markup]):String =
    markups.map(formatMarkup(_)).mkString("")

  def formatMarkup(markup:Markup):String = {
    import Markdown._
    markup match {
      case Markup.Text(content) => content
      case Markup.Paragraph(children) =>
        paragraph(children.map(formatMarkup(_)).mkString(""))
      case Markup.Dl(items) =>
        definitionList(items.map {item => (formatMarkup(item.dt), formatMarkup(item.dd))})
      case Markup.Code(content) =>
        code(content)
      case Markup.CodeInline(content) =>
        inlineCode(content)
      case Markup.LinkInternal(title, id) =>
        link(title, id.fullName)
      case Markup.LinkExternal(title, url) =>
        link(title, url)
      case Markup.Bold(contents) =>
        bold(contents.map(formatMarkup(_)).mkString(""))
      case Markup.Italic(contents) =>
        italic(contents.map(formatMarkup(_)).mkString(""))
    }
  }
}
