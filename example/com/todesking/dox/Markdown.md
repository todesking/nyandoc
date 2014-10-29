# com.todesking.dox.Markdown


```scala
class Markdown extends AnyRef
```


 `def h(level: Int)(str: String): Unit`
----------------------------------------



 `def h2bar(str: String): Unit`
--------------------------------



 `val layout: Layout`
----------------------



 `def link(title: String, url: String): Unit`
----------------------------------------------



 `def normalize(s: String): String`
------------------------------------



 `def normalizeMultiLine(s: String): String`
---------------------------------------------



 `def render(markup: Markup): Unit`
------------------------------------



 `def render(markups: Seq[Markup]): Unit`
------------------------------------------



 `final def !=(arg0: Any): Boolean`
------------------------------------

* Definition Classes
  * Any

(defined at scala.Any#!=(Any):Boolean)


 `final def !=(arg0: AnyRef): Boolean`
---------------------------------------

* Definition Classes
  * AnyRef

(defined at scala.AnyRef#!=(AnyRef):Boolean)


 `final def ##(): Int`
-----------------------

* Definition Classes
  * AnyRef → Any

(defined at scala.AnyRef###():Int)


 `final def ==(arg0: Any): Boolean`
------------------------------------

* Definition Classes
  * Any

(defined at scala.Any#==(Any):Boolean)


 `final def ==(arg0: AnyRef): Boolean`
---------------------------------------

* Definition Classes
  * AnyRef

(defined at scala.AnyRef#==(AnyRef):Boolean)


 `final def asInstanceOf[T0]: T0`
----------------------------------

* Definition Classes
  * Any

(defined at scala.Any#asInstanceOf[T0]:T0)


 `def clone(): AnyRef`
-----------------------

* Attributes
  * protected[java.lang]
* Definition Classes
  * AnyRef
* Annotations
  * @throws (...)

(defined at scala.AnyRef#clone():AnyRef)


 `final def eq(arg0: AnyRef): Boolean`
---------------------------------------

* Definition Classes
  * AnyRef

(defined at scala.AnyRef#eq(AnyRef):Boolean)


 `def equals(arg0: Any): Boolean`
----------------------------------

* Definition Classes
  * AnyRef → Any

(defined at scala.AnyRef#equals(Any):Boolean)


 `def finalize(): Unit`
------------------------

* Attributes
  * protected[java.lang]
* Definition Classes
  * AnyRef
* Annotations
  * @throws (classOf[java.lang.Throwable])

(defined at scala.AnyRef#finalize():Unit)


 `final def getClass(): Class[_]`
----------------------------------

* Definition Classes
  * AnyRef → Any

(defined at scala.AnyRef#getClass():Class[_])


 `def hashCode(): Int`
-----------------------

* Definition Classes
  * AnyRef → Any

(defined at scala.AnyRef#hashCode():Int)


 `final def isInstanceOf[T0]: Boolean`
---------------------------------------

* Definition Classes
  * Any

(defined at scala.Any#isInstanceOf[T0]:Boolean)


 `final def ne(arg0: AnyRef): Boolean`
---------------------------------------

* Definition Classes
  * AnyRef

(defined at scala.AnyRef#ne(AnyRef):Boolean)


 `final def notify(): Unit`
----------------------------

* Definition Classes
  * AnyRef

(defined at scala.AnyRef#notify():Unit)


 `final def notifyAll(): Unit`
-------------------------------

* Definition Classes
  * AnyRef

(defined at scala.AnyRef#notifyAll():Unit)


 `final def synchronized[T0](arg0: ⇒ T0): T0`
----------------------------------------------

* Definition Classes
  * AnyRef

(defined at scala.AnyRef#synchronized[T0](⇒T0):T0)


 `def toString(): String`
--------------------------

* Definition Classes
  * AnyRef → Any

(defined at scala.AnyRef#toString():String)


 `final def wait(): Unit`
--------------------------

* Definition Classes
  * AnyRef
* Annotations
  * @throws (...)

(defined at scala.AnyRef#wait():Unit)


 `final def wait(arg0: Long): Unit`
------------------------------------

* Definition Classes
  * AnyRef
* Annotations
  * @throws (...)

(defined at scala.AnyRef#wait(Long):Unit)


 `final def wait(arg0: Long, arg1: Int): Unit`
-----------------------------------------------

* Definition Classes
  * AnyRef
* Annotations
  * @throws (...)

(defined at scala.AnyRef#wait(Long,Int):Unit)


 `def +(other: String): String`
--------------------------------

* Implicit information
  * This member is added by an implicit conversion from Markdown to StringAdd
    performed by method any2stringadd in scala.Predef.
* Definition Classes
  * StringAdd

(added by implicit convertion: scala.Predef.any2stringadd#+(String):String)


 `def ->[B](y: B): (Markdown, B)`
----------------------------------

* Implicit information
  * This member is added by an implicit conversion from Markdown to ArrowAssoc [
    Markdown] performed by method any2ArrowAssoc in scala.Predef.
* Definition Classes
  * ArrowAssoc
* Annotations
  * @inline ()

(added by implicit convertion: scala.Predef.any2ArrowAssoc#->[B](B):(Markdown,B))


 `def ensuring(cond: (Markdown) ⇒ Boolean): Markdown`
------------------------------------------------------

* Implicit information
  * This member is added by an implicit conversion from Markdown to Ensuring [
    Markdown] performed by method any2Ensuring in scala.Predef.
* Definition Classes
  * Ensuring

(added by implicit convertion: scala.Predef.any2Ensuring#ensuring((Markdown)⇒Boolean):Markdown)


 `def ensuring(cond: (Markdown) ⇒ Boolean, msg: ⇒ Any): Markdown`
------------------------------------------------------------------

* Implicit information
  * This member is added by an implicit conversion from Markdown to Ensuring [
    Markdown] performed by method any2Ensuring in scala.Predef.
* Definition Classes
  * Ensuring

(added by implicit convertion: scala.Predef.any2Ensuring#ensuring((Markdown)⇒Boolean,⇒Any):Markdown)


 `def ensuring(cond: Boolean): Markdown`
-----------------------------------------

* Implicit information
  * This member is added by an implicit conversion from Markdown to Ensuring [
    Markdown] performed by method any2Ensuring in scala.Predef.
* Definition Classes
  * Ensuring

(added by implicit convertion: scala.Predef.any2Ensuring#ensuring(Boolean):Markdown)


 `def ensuring(cond: Boolean, msg: ⇒ Any): Markdown`
-----------------------------------------------------

* Implicit information
  * This member is added by an implicit conversion from Markdown to Ensuring [
    Markdown] performed by method any2Ensuring in scala.Predef.
* Definition Classes
  * Ensuring

(added by implicit convertion: scala.Predef.any2Ensuring#ensuring(Boolean,⇒Any):Markdown)


 `def formatted(fmtstr: String): String`
-----------------------------------------

* Implicit information
  * This member is added by an implicit conversion from Markdown to StringFormat
    performed by method any2stringfmt in scala.Predef.
* Definition Classes
  * StringFormat
* Annotations
  * @inline ()

(added by implicit convertion: scala.Predef.any2stringfmt#formatted(String):String)


 `val self: Any`
-----------------

* Implicit information
  * This member is added by an implicit conversion from Markdown to StringAdd
    performed by method any2stringadd in scala.Predef.
* Shadowing
  * This implicitly inherited member is ambiguous. One or more implicitly
    inherited members have similar signatures, so calling this member may
    produce an ambiguous implicit conversion compiler error. To access this
    member you can use a
    [type ascription](http://stackoverflow.com/questions/2087250/what-is-the-purpose-of-type-ascription-in-scala)
    :
    ```scala
    (markdown: StringAdd).self
    ```
* Definition Classes
  * StringAdd

(added by implicit convertion: scala.Predef.any2stringadd#self:Any)


 `def x: Markdown`
-------------------

* Implicit information
  * This member is added by an implicit conversion from Markdown to ArrowAssoc [
    Markdown] performed by method any2ArrowAssoc in scala.Predef.
* Shadowing
  * This implicitly inherited member is ambiguous. One or more implicitly
    inherited members have similar signatures, so calling this member may
    produce an ambiguous implicit conversion compiler error. To access this
    member you can use a
    [type ascription](http://stackoverflow.com/questions/2087250/what-is-the-purpose-of-type-ascription-in-scala)
    :
    ```scala
    (markdown: ArrowAssoc[Markdown]).x
    ```
* Definition Classes
  * ArrowAssoc
* Annotations
  * @deprecated
* Deprecated
  * _ (Since version 2.10.0) _ Use `leftOfArrow instead`

(added by implicit convertion: scala.Predef.any2ArrowAssoc#x:Markdown)


 `def →[B](y: B): (Markdown, B)`
---------------------------------

* Implicit information
  * This member is added by an implicit conversion from Markdown to ArrowAssoc [
    Markdown] performed by method any2ArrowAssoc in scala.Predef.
* Definition Classes
  * ArrowAssoc

(added by implicit convertion: scala.Predef.any2ArrowAssoc#→[B](B):(Markdown,B))

