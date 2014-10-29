# com.todesking.dox.HtmlParser$


```scala
object HtmlParser
```


 def extractDlMarkup(dl: Node): Seq[Markup]
--------------------------------------------

(defined at com.todesking.dox.HtmlParser#extractDlMarkup(Node):Seq[Markup])


 def extractMarkup(elm: Node): Seq[Markup]
-------------------------------------------

(defined at com.todesking.dox.HtmlParser#extractMarkup(Node):Seq[Markup])


 def extractMarkup0(elm: Node): Seq[Markup]
--------------------------------------------

(defined at com.todesking.dox.HtmlParser#extractMarkup0(Node):Seq[Markup])


 def extractValueMembers(parentId: Id, doc: Document): Seq[Item]
-----------------------------------------------------------------

(defined at com.todesking.dox.HtmlParser#extractValueMembers(Id,Document):Seq[Item])


 def parse(doc: Document): Option[(Item, Seq[Item])]
-----------------------------------------------------

(defined at com.todesking.dox.HtmlParser#parse(Document):Option[(Item,Seq[Item])])


 def parse(file: File, charset: Charset = StandardCharsets.UTF_8): Option[(Item, Seq[Item])]
---------------------------------------------------------------------------------------------

(defined at com.todesking.dox.HtmlParser#parse(File,Charset):Option[(Item,Seq[Item])])

