package com.todesking.nyandoc

case class Exit(val code: Int) extends xsbti.Exit
class Main extends xsbti.AppMain {
  def run(config: xsbti.AppConfiguration) = {
    Exit(Main.run(config.arguments))
  }
}

object Maven {
  implicit class Tap[A](self: A) {
    def tapp(f: A => String = _.toString): A = {println(f(self)); self}
  }
  implicit class Tappo[A](self: Option[A]) {
    def tappo(f: A => String): Option[A] =
      self match {
        case Some(x) => x.tapp(f); self
        case None => "None".tapp(); self
      }
  }
  def findArtifacts(id: String): Seq[MavenArtifact] = {
    import org.json4s._
    import org.json4s.native.JsonMethods._

    val queryS =
      id.split(":") match {
        case Array(g, a) => s"g:$g AND a: $a"
        case Array(g, a, v) => s"g:$g AND a:$a AND v:$v"
        case _ =>
          throw new IllegalArgumentException(s"Maven ID format should <group>:<artifact>(:<version>) but $id")
      }
    println(s"Query: $queryS")

    val data = query("rows" -> "20", "wt" -> "json", "core" -> "gav", "q" -> queryS)
    val result = for {
      JObject(o) <- Some(data)
      JObject(response) <- o.toMap.get("response")
      JArray(docs) <- response.toMap.get("docs")
    } yield for {
      JObject(doc_) <- docs
      doc = doc_.toMap
      JArray(ec) <- doc.get("ec")
      if ec.contains(JString("-javadoc.jar"))
      JString(g) <- doc.get("g")
      JString(a) <- doc.get("a")
      JString(v) <- doc.get("v")
    } yield {
      MavenArtifact(g, a, v)
    }

    result getOrElse(throw new RuntimeException(s"Response format invalid: ${pretty(render(data))}"))
  }

  def query(query: (String, String)*): org.json4s.JsonAST.JValue = {
    import org.json4s._
    import org.json4s.native.JsonMethods._
    val uri = new java.net.URI(s"http://search.maven.org/solrsearch/select?" + buildQueryString(query))
    parse(readContent(uri, "UTF-8"))
  }

  private def readContent(uri: java.net.URI, encoding: String): String = {
    java.net.HttpURLConnection.setFollowRedirects(true)
    val reader = new java.io.BufferedReader(new java.io.InputStreamReader(uri.toURL.openStream(), "UTF-8"))
    val sb = new scala.collection.mutable.StringBuilder
    var line: String = null
    while({line = reader.readLine; line} != null) {
      sb.append(line)
      sb.append("\n")
    }
    sb.toString
  }

  private def buildQueryString(query: Traversable[(String, String)]): String = {
    import java.net.URLEncoder.encode
    val enc = "UTF-8"
    query.map { case(k, v) => s"${encode(k, enc)}=${encode(v, enc)}" } mkString("&")
  }
}

case class MavenArtifact(
  groupId: String,
  artifactId: String,
  version: String
) {
  val id: String = s"${groupId}:${artifactId}:${version}"
  val javadocJarName: String =
    s"${artifactId}-${version}-javadoc.jar"
  val javadocUri: java.net.URI =
    new java.net.URI(s"http://search.maven.org/remotecontent?filepath=${groupId.split("\\.").mkString("/")}/${artifactId}/${version}/${javadocJarName}")
}

object Http {
  def download(uri: java.net.URI): Either[Throwable, java.io.File] = {
    val out = java.io.File.createTempFile("nyandoc", ".jar")
    val in = openStream(uri)
    try {
      java.nio.file.Files.copy(in, out.toPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING)
      Right(out)
    } catch {
      case e: Exception =>
        Left(e)
    } finally {
      in.close()
    }
  }

  def openStream(uri: java.net.URI): java.io.InputStream = {
    // This feature not allow http <=> https redirect
    java.net.HttpURLConnection.setFollowRedirects(true)
    val con = uri.toURL.openConnection() match {
      case c: java.net.HttpURLConnection => c
    }
    con.connect()
    con.getResponseCode() match {
      case c if 300 <= c && c <= 399 =>
        val location = con.getHeaderField("Location")
        openStream(new java.net.URI(location))
      case _ =>
        con.getInputStream()
    }
  }
}

object Main {
  def main(args: Array[String]): Unit = {
    System.exit(run(args))
  }

  import java.io.File

  def selectArtifact(artifacts: Seq[MavenArtifact]): MavenArtifact = {
    val range = 0 until artifacts.size
    def readIntO(): Option[Int] = {
      try {
        Some(scala.io.StdIn.readInt())
      } catch {
        case e: NumberFormatException => None
      }
    }
    def readNumO(): Option[Int] = {
      print(s"Select target artifact[0-${artifacts.size - 1}]: ")
      readIntO() filter(range.contains(_))
    }
    def readNum(): Int = {
      readNumO() getOrElse readNum()
    }
    artifacts.zipWithIndex.foreach { case (a, i) =>
      println(s"${i}) ${a.id}")
    }
    artifacts(readNum())
  }

  def run(args:Array[String]): Int = {
    if(args.size == 3 && args(0) == "--maven") {
      val dest = new File(args(2))
      val artifacts = Maven.findArtifacts(args(1))
      if(artifacts.isEmpty) {
        println(s"[ERROR] Artifact not found in maven.org: ${args(1)}")
        println(s"[ERROR]   Tip: Use * to wildcard search")
        1
      } else {
        val artifact =
          if(artifacts.size > 1)
            selectArtifact(artifacts)
          else
            artifacts.head
        println(s"Downloading ${artifact.id} from ${artifact.javadocUri}")
        Http.download(artifact.javadocUri) match {
          case Left(error) =>
            println(s"Download failed: ${error.toString}")
            1
          case Right(file) =>
            generate(Repository(parse(file)), dest)
            0
        }
      }
    } else if(args.size == 2) {
      val src = new File(args(0))
      val dest = new File(args(1))

      val items = parse(src)
      val repo = Repository(items)

      println(s"generaging documents into ${dest}")
      generate(repo, dest)
      0
    } else {
      println(
        """USAGE: nyandoc <source> <dest-dir>
          |  <source>: dir | jar | zip
          |   OR: nyandoc --maven <mvn_id> <dest-dir>
          |  <mvn_id>: <group_id>:<artifact_id>(:<version>)
        """.stripMargin
      )
      1
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
