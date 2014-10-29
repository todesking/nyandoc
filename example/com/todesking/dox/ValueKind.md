# com.todesking.dox.ValueKind


```scala
sealed abstract class ValueKind extends AnyRef
```


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
  * This member is added by an implicit conversion from ValueKind to StringAdd
    performed by method any2stringadd in scala.Predef.
* Definition Classes
  * StringAdd

(added by implicit convertion: scala.Predef.any2stringadd#+(String):String)


 `def ->[B](y: B): (ValueKind, B)`
-----------------------------------

* Implicit information
  * This member is added by an implicit conversion from ValueKind to ArrowAssoc
    [ValueKind] performed by method any2ArrowAssoc in scala.Predef.
* Definition Classes
  * ArrowAssoc
* Annotations
  * @inline ()

(added by implicit convertion: scala.Predef.any2ArrowAssoc#->[B](B):(ValueKind,B))


 `def ensuring(cond: (ValueKind) ⇒ Boolean): ValueKind`
--------------------------------------------------------

* Implicit information
  * This member is added by an implicit conversion from ValueKind to Ensuring [
    ValueKind] performed by method any2Ensuring in scala.Predef.
* Definition Classes
  * Ensuring

(added by implicit convertion: scala.Predef.any2Ensuring#ensuring((ValueKind)⇒Boolean):ValueKind)


 `def ensuring(cond: (ValueKind) ⇒ Boolean, msg: ⇒ Any): ValueKind`
--------------------------------------------------------------------

* Implicit information
  * This member is added by an implicit conversion from ValueKind to Ensuring [
    ValueKind] performed by method any2Ensuring in scala.Predef.
* Definition Classes
  * Ensuring

(added by implicit convertion: scala.Predef.any2Ensuring#ensuring((ValueKind)⇒Boolean,⇒Any):ValueKind)


 `def ensuring(cond: Boolean): ValueKind`
------------------------------------------

* Implicit information
  * This member is added by an implicit conversion from ValueKind to Ensuring [
    ValueKind] performed by method any2Ensuring in scala.Predef.
* Definition Classes
  * Ensuring

(added by implicit convertion: scala.Predef.any2Ensuring#ensuring(Boolean):ValueKind)


 `def ensuring(cond: Boolean, msg: ⇒ Any): ValueKind`
------------------------------------------------------

* Implicit information
  * This member is added by an implicit conversion from ValueKind to Ensuring [
    ValueKind] performed by method any2Ensuring in scala.Predef.
* Definition Classes
  * Ensuring

(added by implicit convertion: scala.Predef.any2Ensuring#ensuring(Boolean,⇒Any):ValueKind)


 `def formatted(fmtstr: String): String`
-----------------------------------------

* Implicit information
  * This member is added by an implicit conversion from ValueKind to
    StringFormat performed by method any2stringfmt in scala.Predef.
* Definition Classes
  * StringFormat
* Annotations
  * @inline ()

(added by implicit convertion: scala.Predef.any2stringfmt#formatted(String):String)


 `val self: Any`
-----------------

* Implicit information
  * This member is added by an implicit conversion from ValueKind to StringAdd
    performed by method any2stringadd in scala.Predef.
* Shadowing
  * This implicitly inherited member is ambiguous. One or more implicitly
    inherited members have similar signatures, so calling this member may
    produce an ambiguous implicit conversion compiler error. To access this
    member you can use a
    [type ascription](http://stackoverflow.com/questions/2087250/what-is-the-purpose-of-type-ascription-in-scala)
    :
    ```scala
    (valueKind: StringAdd).self
    ```
* Definition Classes
  * StringAdd

(added by implicit convertion: scala.Predef.any2stringadd#self:Any)


 `def x: ValueKind`
--------------------

* Implicit information
  * This member is added by an implicit conversion from ValueKind to ArrowAssoc
    [ValueKind] performed by method any2ArrowAssoc in scala.Predef.
* Shadowing
  * This implicitly inherited member is ambiguous. One or more implicitly
    inherited members have similar signatures, so calling this member may
    produce an ambiguous implicit conversion compiler error. To access this
    member you can use a
    [type ascription](http://stackoverflow.com/questions/2087250/what-is-the-purpose-of-type-ascription-in-scala)
    :
    ```scala
    (valueKind: ArrowAssoc[ValueKind]).x
    ```
* Definition Classes
  * ArrowAssoc
* Annotations
  * @deprecated
* Deprecated
  * _ (Since version 2.10.0) _ Use `leftOfArrow instead`

(added by implicit convertion: scala.Predef.any2ArrowAssoc#x:ValueKind)


 `def →[B](y: B): (ValueKind, B)`
----------------------------------

* Implicit information
  * This member is added by an implicit conversion from ValueKind to ArrowAssoc
    [ValueKind] performed by method any2ArrowAssoc in scala.Predef.
* Definition Classes
  * ArrowAssoc

(added by implicit convertion: scala.Predef.any2ArrowAssoc#→[B](B):(ValueKind,B))

