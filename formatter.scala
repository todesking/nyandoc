package com.todesking.dox

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
