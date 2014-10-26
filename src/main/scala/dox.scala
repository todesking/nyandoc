package com.todesking.dox

object Crawler {
  def crawl(rootUrl:java.net.URL):Seq[Item] = ???
  def crawlLocal(root:java.io.File):Seq[Item] = ???
}

object Main {
  import java.io.File

  def main(args:Array[String]):Unit = {
    if(args.size != 2) {
      println("USAGE: $0 <scaladoc-dir|scaladoc-html> <dest-dir>")
    } else {
      val src = new File(args(0))
      val dest = new File(args(1))

      val items = parse(src)
      val repo = Repository(items)

      generate(items, repo, dest)
    }
  }

  def parse(file:File):Seq[Item] = {
    if(file.isDirectory) {
      file.listFiles.flatMap(parse(_))
    } else if(file.getName.endsWith(".html")){
      println(s"Processing: ${file}")
      parse0(file)
    } else {
      Seq()
    }
  }

  def parse0(file:File):Seq[Item] = {
    HtmlParser.parse(file)
  }

  def generate(items:Seq[Item], repo:Repository, dest:File):Unit = {
    if(!dest.exists)
      dest.mkdirs()

    items.foreach {item =>
      if(repo.isTopLevel(item))
        generate0(item, repo, dest)
    }
  }

  def generate0(top:Item, repo:Repository, destDir:File):Unit = {
    val content = new PlainTextFormatter().format(top, repo)

    import java.io._
    val dest:File = new File(destDir, top.id.asFileName)
    println()
    println(s"===================== ${dest} ========================")
    println(content)
    return

    val writer = new BufferedWriter(new FileWriter(dest))
    try {
      writer.write(content)
    } finally {
      writer.close()
    }
  }
}
