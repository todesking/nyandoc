package com.todesking.nyandoc

object Crawler {
  def crawl(rootUrl:java.net.URL):Seq[Item] = ???
  def crawlLocal(root:java.io.File):Seq[Item] = ???
}

case class Exit(val code: Int) extends xsbti.Exit
class Main extends xsbti.AppMain {
  def run(config: xsbti.AppConfiguration) = {
    Exit(Main.run(config.arguments))
  }
}

object Main {
  def main(args: Array[String]): Unit = {
    System.exit(run(args))
  }

  import java.io.File

  def run(args:Array[String]): Int = {
    if(args.size != 2) {
      println("USAGE: $0 <scaladoc-dir|scaladoc-html> <dest-dir>")
      1
    } else {
      val src = new File(args(0))
      val dest = new File(args(1))

      val items = parse(src)
      val repo = Repository(items)

      println(s"generaging documents into ${dest}")
      generate(repo, dest)
      0
    }
  }

  def parse(file:File):Seq[HtmlParser.Result] = {
    if(file.isDirectory) {
      file.listFiles.flatMap(parse(_))
    } else if(!file.exists()) {
      println(s"WARN: Not found: $file")
      Seq()
    } else if(file.getName.endsWith(".html")){
      println(s"Processing: ${file}")
      HtmlParser.parse(file).toSeq
    } else if(file.getName.endsWith(".jar") || file.getName.endsWith(".zip")) {
      println(s"Processing: ${file}")
      parseZip(file)
    } else {
      println(s"Skip: $file")
      Seq()
    }
  }

  def parse0(file:File):Option[HtmlParser.Result] = {
    HtmlParser.parse(file)
  }

  def parseZip(file:File):Seq[HtmlParser.Result] = {
    import java.util.zip._
    import java.io._
    import IOExt._

    val jis = new ZipInputStream(new FileInputStream(file))
    val reader = new BufferedReader(new InputStreamReader(jis))
    val results = scala.collection.mutable.ArrayBuffer.empty[HtmlParser.Result]
    try {
      var entry:ZipEntry = null
      while({entry = jis.getNextEntry(); entry != null}) {
        if(entry.getName.endsWith(".html")) {
          println(s"Processing: ${entry.getName}")
          results ++= HtmlParser.parse(jis.readAll()).toSeq
        }
      }
      results.toSeq
    } finally {
      jis.close()
    }
  }

  def generate(repo:Repository, dest:File):Unit = {
    if(!dest.exists)
      dest.mkdirs()

    repo.allItems.filter{item => repo.childrenOf(item).nonEmpty }.foreach {item =>
      generate0(item, repo, dest)
    }
  }

  def generate0(top:Item, repo:Repository, destDir:File):Unit = {
    val content = new MarkdownFormatter().format(top, repo)

    import java.io._
    val dest:File = destFileOf(destDir, top)

    println(s"Generating ${dest}")
    dest.getParentFile.mkdirs()

    val writer = new BufferedWriter(new FileWriter(dest))
    try {
      writer.write(content)
    } finally {
      writer.close()
    }
  }

  def destFileOf(dir:java.io.File, item: Item):java.io.File = {
    def idToS(id:Id): Seq[String] = id match {
      case Id.Root =>
        Seq()
      case tid: Id.ChildType =>
        idToS(tid.parentId) ++ Seq(tid.localName)
      case vid: Id.ChildValue =>
        idToS(vid.parentId) :+ vid.localName
      case vid: Id.ChildMethod =>
        idToS(vid.parentId) :+ vid.localName
    }
    new java.io.File(dir, idToS(item.id).mkString("/") + (item match { case _: Object => "$.md" case _ => ".md"}))
  }
}
