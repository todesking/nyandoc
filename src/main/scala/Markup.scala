package com.todesking.nyandoc

sealed abstract class Markup
object Markup {

  // Paragraph/Block
  case class Heading(contents:Seq[Markup]) extends Markup
  case class Paragraph(children:Seq[Markup]) extends Markup
  case class Code(content:String, language:String) extends Markup
  case class HorizontalLine() extends Markup
  case class BlockQuote(contents: Seq[Markup]) extends Markup
  case class Table(rows: Seq[TableRow]) extends Markup
  case class TableRow(cols: Seq[TableColumn])
  case class TableColumn(contents: Seq[Markup])

  // List
  case class Dl(items:Seq[DlItem]) extends Markup
  case class DlItem(dt:Seq[Markup], dd:Seq[Markup])
  case class UnorderedList(items:Seq[ListItem]) extends Markup
  case class OrderedList(items:Seq[ListItem]) extends Markup
  case class ListItem(contents:Seq[Markup])

  // Inline
  case class LinkInternal(title:String, id:String) extends Markup // TODO: id:Id
  case class LinkExternal(title:String, url:String) extends Markup
  case class Text(content:String) extends Markup
  case class CodeInline(content:String) extends Markup
  case class Bold(contents:Seq[Markup]) extends Markup
  case class Italic(contents:Seq[Markup]) extends Markup
  case class Sup(contents:Seq[Markup]) extends Markup

  def normalize(markups:Seq[Markup]):Seq[Markup] =
    Normalize(markups)

  object Normalize {
    def apply(markups:Seq[Markup]):Seq[Markup] =
      doRecursive(dropEmpty _ andThen removeEmptyLink andThen removeMeaninglessHR andThen textFusion)(markups)

    def doRecursive(f:Seq[Markup]=>Seq[Markup])(markups:Seq[Markup]):Seq[Markup] = {
      def doR(ms:Seq[Markup]) = doRecursive(f)(ms)
      f(
        markups map {
          case Paragraph(cs) => Paragraph(doRecursive(f)(cs))
          case BlockQuote(cs) => BlockQuote(doRecursive(f)(cs))
          case Dl(items) => Dl(items map {item => DlItem(doR(item.dt), doR(item.dd))})
          case UnorderedList(items) => UnorderedList(items map {item => ListItem(doR(item.contents))})
          case Bold(contents) => Bold(doR(contents))
          case Italic(contents) => Italic(doR(contents))
          case Heading(contents) => Heading(doR(contents))
          case Table(rows) => Table(rows.map {row => TableRow(row.cols.map {col => TableColumn(doRecursive(f)(col.contents)) }) })
          case other => other
        }
      )
    }

    def dropEmpty(markups:Seq[Markup]) = {
      val empty = Seq()
      markups.flatMap {
        case
          Text("")
          | Paragraph(Seq())
          | Table(Seq())
          | Dl(Seq())
          | UnorderedList(Seq())
          | Code("", _)
          | CodeInline("")
          | Bold(Seq())
          | Italic(Seq())
          | Heading(Seq())
          =>
            Seq()
        case other => Seq(other)
      }
    }

    def removeEmptyLink(markups:Seq[Markup]) = markups.map {
      case LinkExternal(title, "") =>
        Text(title)
      case x =>
        x
    }

    def removeMeaninglessHR(markups: Seq[Markup]): Seq[Markup] = {
      if(markups.size < 2) markups
      else {
        (markups.head +: removeMeaninglessHR(markups.tail)).toList match {
          case HorizontalLine() :: HorizontalLine() :: xs =>
            HorizontalLine() +: xs
          case HorizontalLine() :: (code@Code(_, _)) :: HorizontalLine() :: xs =>
            code +: xs
          case xs =>
            xs
        }

      }
    }

    def textFusion(markups:Seq[Markup]): Seq[Markup] =
      if(markups.size < 2) markups
      else {
        val tail = textFusion(markups.tail)
        (markups.head, tail.head) match {
          case (Markup.Text(c1), Markup.Text(c2)) =>
            Markup.Text(c1 + " " + c2) +: tail.tail
          case _ => markups.head +: tail
        }
      }
  }
}

