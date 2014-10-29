package com.todesking.dox

class Layout(optimalWidth:Int, private var indentLevel:Int) {
  import scala.collection.mutable
  private var lines = mutable.ArrayBuffer.empty[String]
  private var currentLine:String = ""

  override def toString() =
    lines.mkString("\n") + currentLine + "\n"

  def appendText(str:String):Unit =
    doMultiLine(str)(appendBreakable0)

  def appendUnbreakable(str:String):Unit =
    doMultiLine(str)(appendUnbreakable0(_, needSpacing = true))

  private[this] def doMultiLine(str:String)(f:String => Unit):Unit = {
    val lines = str.split("\n")
    assert(lines.nonEmpty)
    lines.dropRight(1).foreach {line =>
      f(line)
      newLine()
    }
    f(lines.head)
  }

  private[this] def hasCurrentLineContent():Boolean =
    currentLine.nonEmpty && currentLine != " " * indentLevel

  def indent(n:Int):Unit = {
    require(indentLevel + n >= 0)
    if(!hasCurrentLineContent) {
      indentLevel += n
      currentLine = " " * indentLevel
    } else {
      indentLevel += n
    }
  }

  def terminateLine():Unit = {
    if(hasCurrentLineContent) {
      newLine()
    } else {
      if(lines.nonEmpty && lines.last.isEmpty)
        lines = lines.dropRight(1)
    }
  }

  def newLine():Unit = {
    lines += currentLine.replaceAll("""\s+$""", "")
    currentLine = " " * indentLevel
  }

  private[this] def appendBreakable0(str:String):Unit = {
    val words = str.split("""\s+""").filter(_.nonEmpty)
    words.foreach { word =>
      appendUnbreakable0(word, needSpacing = true)
    }
  }

  private[this] def appendUnbreakable0(str:String, needSpacing:Boolean):Unit = {
    if(!hasCurrentLineContent) {
      currentLine += str
    } else if(needSpacing && needSpacingBeyond(currentLine.last, str)) {
      val spaced = " " + str
      if(needNewLine(width(spaced))) {
        newLine()
        currentLine += str
      } else {
        currentLine += spaced
      }
    } else if(needNewLine(width(str)) && canNextLine(str)) {
      newLine()
      currentLine += str
    } else {
      currentLine += str
    }
  }

  private[this] def needSpacingBeyond(c:Char, s:String):Boolean = {
    (s(0) match {
      case ' ' | ')' | ']' | ';' | '.' | ',' => false
      case _ => true
    }) && (c match {
      case ' ' | '[' | '(' => false
      case _ => true
    })
  }

  private[this] def needNewLine(w:Int):Boolean =
    indentLevel < width(currentLine) && w + width(currentLine) > optimalWidth

  private[this] def canNextLine(s:String):Boolean =
    "!.,:;)]}>".contains(s(0)).unary_!

  private[this] def width(line:String):Int = {
    line.length
  }
}

class Markdown(val layout:Layout = new Layout(80, 0)) {
  def render(markups:Seq[Markup]):Unit =
    markups.foreach(render(_))

  def render(markup:Markup):Unit = {
    markup match {
      case Markup.Text(content) =>
        layout.appendText(normalize(content))
      case Markup.Paragraph(children) =>
        children.foreach {c => render(c) }
        layout.terminateLine()
        layout.newLine()
      case Markup.Dl(items) =>
        items.foreach {item =>
          layout.appendText("* ")
          layout.indent(2)
          render(item.dt)
          layout.indent(-2)
          layout.terminateLine()

          layout.indent(2)
          layout.appendText("* ")
          layout.indent(2)
          render(item.dd)
          layout.indent(-2 * 2)
          layout.terminateLine()
        }
        layout.newLine()
      case Markup.Code(content) =>
        layout.newLine()
        layout.appendUnbreakable("```scala")
        layout.newLine()
        layout.appendUnbreakable(normalizeMultiLine(content))
        layout.newLine()
        layout.appendUnbreakable("```")
        layout.newLine()
        layout.newLine()
      case Markup.CodeInline(content) =>
        layout.appendUnbreakable("`" + normalize(content) + "`")
      case Markup.LinkInternal(title, id) =>
        layout.appendText(normalize(title))
      case Markup.LinkExternal(title, url) =>
        link(title, url)
      case Markup.Bold(contents) =>
        layout.appendUnbreakable("*")
        render(contents)
        layout.appendUnbreakable("*")
      case Markup.Italic(contents) =>
        layout.appendUnbreakable("_")
        render(contents)
        layout.appendUnbreakable("_")
      case Markup.UnorderedList(items) =>
        layout.newLine()
        items.foreach {item =>
          layout.appendUnbreakable("* ")
          layout.indent(2)
          render(item.contents)
          layout.indent(-2)
          layout.terminateLine()
        }
    }
  }

  def normalize(s:String):String =
    """(?m)\s+""".r.replaceAllIn(s, " ")

  def normalizeMultiLine(s:String) =
    """(?m)\A\n+|\n+\z""".r.replaceAllIn(s, "")

  def h(level:Int)(str:String):Unit = {
    layout.appendUnbreakable(("#" * level) + " " + str)
    layout.newLine()
    layout.newLine()
  }

  def h2bar(str:String):Unit = {
    layout.appendUnbreakable(s" ${str}")
    layout.newLine()
    layout.appendUnbreakable(s"${"-" * (str.length + 2)}")
    layout.newLine()
    layout.newLine()
  }

  def link(title:String, url:String):Unit = {
    layout.appendUnbreakable(s"[${title}](${url})")
  }
}

class MarkdownFormatter {
  def format(item:Item, repo:Repository):String = {
    val renderer = new Markdown()

    renderer.h(1)(item.id.fullName)
    renderer.render(Markup.Code(item.signature))
    renderer.render(item.comment)

    repo.childrenOf(item).sortBy {child =>
      val orderByKind =
        child match {
          case _:DefinedMethod => 0
          case _:ViaInheritMethod => 1
          case _:ViaImplicitMethod => 2
          case _ => 100
        }
      (orderByKind, child.id.fullName)
    }.filter {
      case item:ViaInheritMethod =>
        Seq("scala.Any", "scala.AnyRef").contains(item.originalId match { case c:Id.Child => c.parentId.fullName; case _ => "" }).unary_!
      case _:ViaImplicitMethod => false
      case _ => true
    }.foreach {child =>
      renderer.layout.newLine()
      renderer.h2bar(child.signature)
      renderer.render(child.comment)

      child match {
        case c:ViaImplicitMethod =>
          renderer.layout.appendUnbreakable(s"(added by implicit convertion: ${c.originalId})")
          renderer.layout.newLine()
        case c:ViaInheritMethod =>
          renderer.layout.appendUnbreakable(s"(defined at ${c.originalId})")
          renderer.layout.newLine()
        case _ =>
      }
      renderer.layout.newLine()
    }

    renderer.layout.toString()

  }
}
