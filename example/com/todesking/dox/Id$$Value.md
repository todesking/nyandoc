# com.todesking.dox.Id$$Value


```scala
sealed trait Value extends Id
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


 `def childFullName(name: String): String`
-------------------------------------------

* Definition Classes
  * Value → Id

(defined at com.todesking.dox.Id.Value#childFullName(String):String)


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


 `abstract def fullName(): String`
-----------------------------------

* Definition Classes
  * Id

(defined at com.todesking.dox.Id#fullName():String)


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


 `abstract def isAncestorOf(id: Id): Boolean`
----------------------------------------------

* Definition Classes
  * Id

(defined at com.todesking.dox.Id#isAncestorOf(Id):Boolean)


 `final def isInstanceOf[T0]: Boolean`
---------------------------------------

* Definition Classes
  * Any

(defined at scala.Any#isInstanceOf[T0]:Boolean)


 `abstract def isParentOf(id: Id): Boolean`
--------------------------------------------

* Definition Classes
  * Id

(defined at com.todesking.dox.Id#isParentOf(Id):Boolean)


 `abstract def localName(): String`
------------------------------------

* Definition Classes
  * Id

(defined at com.todesking.dox.Id#localName():String)


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


 `abstract def relNameFrom(base: Id): String`
----------------------------------------------

* Definition Classes
  * Id

(defined at com.todesking.dox.Id#relNameFrom(Id):String)


 `final def synchronized[T0](arg0: ⇒ T0): T0`
----------------------------------------------

* Definition Classes
  * AnyRef

(defined at scala.AnyRef#synchronized[T0](⇒T0):T0)


 `def toString(): String`
--------------------------

* Definition Classes
  * Id → AnyRef → Any

(defined at com.todesking.dox.Id#toString():String)


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
  * This member is added by an implicit conversion from Value to StringAdd
    performed by method any2stringadd in scala.Predef.
* Definition Classes
  * StringAdd

(added by implicit convertion: scala.Predef.any2stringadd#+(String):String)


 `def ->[B](y: B): (Value, B)`
-------------------------------

* Implicit information
  * This member is added by an implicit conversion from Value to ArrowAssoc [
    Value] performed by method any2ArrowAssoc in scala.Predef.
* Definition Classes
  * ArrowAssoc
* Annotations
  * @inline ()

(added by implicit convertion: scala.Predef.any2ArrowAssoc#->[B](B):(Value,B))


 `def ensuring(cond: (Value) ⇒ Boolean): Value`
------------------------------------------------

* Implicit information
  * This member is added by an implicit conversion from Value to Ensuring [Value]
    performed by method any2Ensuring in scala.Predef.
* Definition Classes
  * Ensuring

(added by implicit convertion: scala.Predef.any2Ensuring#ensuring((Value)⇒Boolean):Value)


 `def ensuring(cond: (Value) ⇒ Boolean, msg: ⇒ Any): Value`
------------------------------------------------------------

* Implicit information
  * This member is added by an implicit conversion from Value to Ensuring [Value]
    performed by method any2Ensuring in scala.Predef.
* Definition Classes
  * Ensuring

(added by implicit convertion: scala.Predef.any2Ensuring#ensuring((Value)⇒Boolean,⇒Any):Value)


 `def ensuring(cond: Boolean): Value`
--------------------------------------

* Implicit information
  * This member is added by an implicit conversion from Value to Ensuring [Value]
    performed by method any2Ensuring in scala.Predef.
* Definition Classes
  * Ensuring

(added by implicit convertion: scala.Predef.any2Ensuring#ensuring(Boolean):Value)


 `def ensuring(cond: Boolean, msg: ⇒ Any): Value`
--------------------------------------------------

* Implicit information
  * This member is added by an implicit conversion from Value to Ensuring [Value]
    performed by method any2Ensuring in scala.Predef.
* Definition Classes
  * Ensuring

(added by implicit convertion: scala.Predef.any2Ensuring#ensuring(Boolean,⇒Any):Value)


 `def formatted(fmtstr: String): String`
-----------------------------------------

* Implicit information
  * This member is added by an implicit conversion from Value to StringFormat
    performed by method any2stringfmt in scala.Predef.
* Definition Classes
  * StringFormat
* Annotations
  * @inline ()

(added by implicit convertion: scala.Predef.any2stringfmt#formatted(String):String)


 `val self: Any`
-----------------

* Implicit information
  * This member is added by an implicit conversion from Value to StringAdd
    performed by method any2stringadd in scala.Predef.
* Shadowing
  * This implicitly inherited member is ambiguous. One or more implicitly
    inherited members have similar signatures, so calling this member may
    produce an ambiguous implicit conversion compiler error. To access this
    member you can use a
    [type ascription](http://stackoverflow.com/questions/2087250/what-is-the-purpose-of-type-ascription-in-scala)
    :
    ```scala
    (value: StringAdd).self
    ```
* Definition Classes
  * StringAdd

(added by implicit convertion: scala.Predef.any2stringadd#self:Any)


 `def x: Value`
----------------

* Implicit information
  * This member is added by an implicit conversion from Value to ArrowAssoc [
    Value] performed by method any2ArrowAssoc in scala.Predef.
* Shadowing
  * This implicitly inherited member is ambiguous. One or more implicitly
    inherited members have similar signatures, so calling this member may
    produce an ambiguous implicit conversion compiler error. To access this
    member you can use a
    [type ascription](http://stackoverflow.com/questions/2087250/what-is-the-purpose-of-type-ascription-in-scala)
    :
    ```scala
    (value: ArrowAssoc[Value]).x
    ```
* Definition Classes
  * ArrowAssoc
* Annotations
  * @deprecated
* Deprecated
  * _ (Since version 2.10.0) _ Use `leftOfArrow instead`

(added by implicit convertion: scala.Predef.any2ArrowAssoc#x:Value)


 `def →[B](y: B): (Value, B)`
------------------------------

* Implicit information
  * This member is added by an implicit conversion from Value to ArrowAssoc [
    Value] performed by method any2ArrowAssoc in scala.Predef.
* Definition Classes
  * ArrowAssoc

(added by implicit convertion: scala.Predef.any2ArrowAssoc#→[B](B):(Value,B))

