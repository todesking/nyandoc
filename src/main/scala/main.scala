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

      println(s"generaging documents into ${dest}")
      generate(repo, dest)
    }
  }

  def parse(file:File):Seq[(Item, Seq[Item])] = {
    if(file.isDirectory) {
      file.listFiles.flatMap(parse(_))
    } else if(file.getName.endsWith(".html")){
      println(s"Processing: ${file}")
      parse0(file).toSeq
    } else {
      Seq()
    }
  }

  def parse0(file:File):Option[(Item, Seq[Item])] = {
    HtmlParser.parse(file)
  }

  def generate(repo:Repository, dest:File):Unit = {
    if(!dest.exists)
      dest.mkdirs()

    repo.topLevelItems.foreach {item =>
      generate0(item, repo, dest)
    }
  }

  def generate0(top:Item, repo:Repository, destDir:File):Unit = {
    val content = new MarkdownFormatter().format(top, repo)

    import java.io._
    val dest:File = destFileOf(destDir, top.id)

    println(s"Generating ${dest}")
    dest.getParentFile.mkdirs()

    val writer = new BufferedWriter(new FileWriter(dest))
    try {
      writer.write(content)
    } finally {
      writer.close()
    }
  }

  def destFileOf(dir:java.io.File, id:Id):java.io.File = {
    id match {
      case _:Id.Type =>
        new java.io.File(dir, id.fullName.replaceAll("""\.""", "/") + ".md")
      case _:Id.Value =>
        new java.io.File(dir, id.fullName.replaceAll("""\.""", "/") + "$.md")
    }
  }
}
