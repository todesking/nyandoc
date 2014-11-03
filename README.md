# Dox: scaladoc HTML parser

## About

*Goal:* To parse javadoc/scaladoc HTML and generate the document with specific format.

*Motivation:* I need Scala API documentation with more text-editor friendly format than HTML.
But Scaladoc only support to generate HTML. "doclet" feature is provided, but it is very restricted and difficult to understand.
So I don't generate document from sources directly, but instead parse HTML and transform it.

## Current status

* Basic features are supported
	* Parse scaladoc to internal representation
	* Parse javadoc to internal representation
	* Generate markdown documents from internal representation
* Not supported yet
	* Other output format
	* Old-format javadoc input
	* Some html tags (ol, blockquote, table, image)
	* Text layout engine is not support for fullwidth characters.

## Usage

Currently, output format fixed to markdown.

```shell-session
$ sbt 'run <source-location> <dest-dir>
```

`source-location` could be directory, html, jar, or zip.
File type is determined by its extension.


## Links

* Scala API Documentation
	* [2.11.4](http://scala-lang.org/download/2.11.4.html)
* JDK API Documentation
	* [JDK8](http://www.oracle.com/technetwork/java/javase/documentation/jdk8-doc-downloads-2133158.html)
	* [JDK7](http://www.oracle.com/technetwork/java/javase/documentation/java-se-7-doc-download-435117.html)
	* [Japanese document(1.4 - 8)](http://www.oracle.com/technetwork/jp/java/java-sun-1440465-ja.html)
* Java/Scala library documentation
	* Jar-packed documents often found in [maven.org](http://search.maven.org)
