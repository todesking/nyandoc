# Dox: scaladoc HTML parser

Goal: To parse javadoc/scaladoc HTML and generate the document with specific format.

Motivation: I need Scala API documentation with more text-editor friendly format than HTML.
But Scaladoc only support to generate HTML. "doclet" feature is provided, but it is very restricted and difficult to understand.
So I don't generate document from sources directly, but instead parse HTML and transform it.

