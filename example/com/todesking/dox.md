# com.todesking.dox


```scala
dox
```


 `object Crawler`
------------------



 `object HtmlParser`
---------------------



 `sealed trait Id extends AnyRef`
----------------------------------



 `object Id`
-------------



 `sealed abstract class Child extends Id`
------------------------------------------



 `sealed case class ChildMethod(parentId: Id, localBaseName: String, paramId: String) extends Child with Value with Product with Serializable`
-----------------------------------------------------------------------------------------------------------------------------------------------



 `sealed case class ChildType(parentId: Id, localName: String) extends Child with Type with Product with Serializable`
-----------------------------------------------------------------------------------------------------------------------



 `case class ChildValue(parentId: Id, localName: String) extends Child with Value with Product with Serializable`
------------------------------------------------------------------------------------------------------------------



 `sealed abstract class Root extends Id`
-----------------------------------------



 `object Root extends Root with Value`
---------------------------------------



 `sealed trait Type extends Id`
--------------------------------



 `sealed trait Value extends Id`
---------------------------------



 `sealed abstract class Item extends AnyRef`
---------------------------------------------



 `sealed class ItemKind extends AnyRef`
----------------------------------------



 `object ItemKind`
-------------------



 `object Type extends ItemKind`
--------------------------------



 `object Value extends ItemKind`
---------------------------------



 `object JsoupExt`
-------------------



 `implicit class ElementExt[A <: Element] extends AnyRef`
----------------------------------------------------------



 `implicit class ElementsExt[A <: Elements] extends AnyRef`
------------------------------------------------------------



 `object Tag`
--------------



 `object TextCleaner`
----------------------



 `implicit class TextNodeExt[A <: TextNode] extends AnyRef`
------------------------------------------------------------



 `class Layout extends AnyRef`
-------------------------------



 `object LibGlobal`
--------------------

utility functions that globally used in the librery



 `object Main`
---------------



 `class Markdown extends AnyRef`
---------------------------------



 `class MarkdownFormatter extends AnyRef`
------------------------------------------



 `sealed abstract class Markup extends AnyRef`
-----------------------------------------------



 `object Markup`
-----------------



 `case class Bold(contents: Seq[Markup]) extends Markup with Product with Serializable`
----------------------------------------------------------------------------------------



 `case class Code(content: String) extends Markup with Product with Serializable`
----------------------------------------------------------------------------------



 `case class CodeInline(content: String) extends Markup with Product with Serializable`
----------------------------------------------------------------------------------------



 `case class Dl(items: Seq[DlItem]) extends Markup with Product with Serializable`
-----------------------------------------------------------------------------------



 `case class DlItem(dt: Seq[Markup], dd: Seq[Markup]) extends Product with Serializable`
-----------------------------------------------------------------------------------------



 `case class Italic(contents: Seq[Markup]) extends Markup with Product with Serializable`
------------------------------------------------------------------------------------------



 `case class LinkExternal(title: String, url: String) extends Markup with Product with Serializable`
-----------------------------------------------------------------------------------------------------



 `case class LinkInternal(title: String, id: String) extends Markup with Product with Serializable`
----------------------------------------------------------------------------------------------------



 `case class ListItem(contents: Seq[Markup]) extends Product with Serializable`
--------------------------------------------------------------------------------



 `object Normalize`
--------------------



 `case class Paragraph(children: Seq[Markup]) extends Markup with Product with Serializable`
---------------------------------------------------------------------------------------------



 `case class Text(content: String) extends Markup with Product with Serializable`
----------------------------------------------------------------------------------



 `case class UnorderedList(items: Seq[ListItem]) extends Markup with Product with Serializable`
------------------------------------------------------------------------------------------------



 `abstract class Method extends Value`
---------------------------------------



 `case class MethodParams(signature: String) extends Product with Serializable`
--------------------------------------------------------------------------------



 `case class Object(id: Id.Value, signature: String, comment: Seq[Markup]) extends Value with Product with Serializable`
-------------------------------------------------------------------------------------------------------------------------



 `case class Package(id: Id.Value, comment: Seq[Markup]) extends Value with Product with Serializable`
-------------------------------------------------------------------------------------------------------



 `trait Repository extends AnyRef`
-----------------------------------



 `object Repository`
---------------------



 `case class ResultType(signature: String) extends Product with Serializable`
------------------------------------------------------------------------------



 `sealed class Scope extends AnyRef`
-------------------------------------



 `object Scope`
----------------



 `case class Private() extends Scope with Product with Serializable`
---------------------------------------------------------------------



 `case class Protected() extends Scope with Product with Serializable`
-----------------------------------------------------------------------



 `case class Public() extends Scope with Product with Serializable`
--------------------------------------------------------------------



 `case class Type(id: Id.Type, kind: TypeKind, signature: String, comment: Seq[Markup]) extends Item with Product with Serializable`
-------------------------------------------------------------------------------------------------------------------------------------



 `sealed class TypeKind extends AnyRef`
----------------------------------------



 `object TypeKind`
-------------------



 `object CaseClass extends TypeKind with Product with Serializable`
--------------------------------------------------------------------



 `object Class extends TypeKind with Product with Serializable`
----------------------------------------------------------------



 `object Trait extends TypeKind with Product with Serializable`
----------------------------------------------------------------



 `object Type extends TypeKind with Product with Serializable`
---------------------------------------------------------------



 `sealed class Value extends Item`
-----------------------------------



 `sealed abstract class ValueKind extends AnyRef`
--------------------------------------------------



 `object ValueKind`
--------------------



 `object Def extends ValueKind with Product with Serializable`
---------------------------------------------------------------



 `object Object extends ValueKind with Product with Serializable`
------------------------------------------------------------------



 `object Package extends ValueKind with Product with Serializable`
-------------------------------------------------------------------



 `object Val extends ValueKind with Product with Serializable`
---------------------------------------------------------------



 `object Var extends ValueKind with Product with Serializable`
---------------------------------------------------------------



 `case class ViaImplicitMethod(id: Id.Value, params: MethodParams, resultType: ResultType, signature: String, originalId: Id.Value, comment: Seq[Markup]) extends Method with Product with Serializable`
---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



 `case class ViaInheritMethod(id: Id.Value, params: MethodParams, resultType: ResultType, signature: String, originalId: Id.Value, comment: Seq[Markup]) extends Method with Product with Serializable`
--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


