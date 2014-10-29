# com.todesking.dox.ViaInheritMethod


```scala
case class ViaInheritMethod(id: Id.Value, params: MethodParams, resultType: ResultType, signature: String, originalId: Id.Value, comment: Seq[Markup]) extends Method with Product with Serializable
```


 `val comment: Seq[Markup]`
----------------------------

* Definition Classes
  * ViaInheritMethod → Item



 `val id: Id.Value`
--------------------

* Definition Classes
  * ViaInheritMethod → Item



 `val originalId: Id.Value`
----------------------------



 `val params: MethodParams`
----------------------------



 `val resultType: ResultType`
------------------------------



 `val signature: String`
-------------------------

* Definition Classes
  * ViaInheritMethod → Method → Item



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
  * This member is added by an implicit conversion from ViaInheritMethod to
    StringAdd performed by method any2stringadd in scala.Predef.
* Definition Classes
  * StringAdd

(added by implicit convertion: scala.Predef.any2stringadd#+(String):String)


 `def ->[B](y: B): (ViaInheritMethod, B)`
------------------------------------------

* Implicit information
  * This member is added by an implicit conversion from ViaInheritMethod to
    ArrowAssoc [ViaInheritMethod] performed by method any2ArrowAssoc in
    scala.Predef.
* Definition Classes
  * ArrowAssoc
* Annotations
  * @inline ()

(added by implicit convertion: scala.Predef.any2ArrowAssoc#->[B](B):(ViaInheritMethod,B))


 `def ensuring(cond: (ViaInheritMethod) ⇒ Boolean): ViaInheritMethod`
----------------------------------------------------------------------

* Implicit information
  * This member is added by an implicit conversion from ViaInheritMethod to
    Ensuring [ViaInheritMethod] performed by method any2Ensuring in
    scala.Predef.
* Definition Classes
  * Ensuring

(added by implicit convertion: scala.Predef.any2Ensuring#ensuring((ViaInheritMethod)⇒Boolean):ViaInheritMethod)


 `def ensuring(cond: (ViaInheritMethod) ⇒ Boolean, msg: ⇒ Any): ViaInheritMethod`
----------------------------------------------------------------------------------

* Implicit information
  * This member is added by an implicit conversion from ViaInheritMethod to
    Ensuring [ViaInheritMethod] performed by method any2Ensuring in
    scala.Predef.
* Definition Classes
  * Ensuring

(added by implicit convertion: scala.Predef.any2Ensuring#ensuring((ViaInheritMethod)⇒Boolean,⇒Any):ViaInheritMethod)


 `def ensuring(cond: Boolean): ViaInheritMethod`
-------------------------------------------------

* Implicit information
  * This member is added by an implicit conversion from ViaInheritMethod to
    Ensuring [ViaInheritMethod] performed by method any2Ensuring in
    scala.Predef.
* Definition Classes
  * Ensuring

(added by implicit convertion: scala.Predef.any2Ensuring#ensuring(Boolean):ViaInheritMethod)


 `def ensuring(cond: Boolean, msg: ⇒ Any): ViaInheritMethod`
-------------------------------------------------------------

* Implicit information
  * This member is added by an implicit conversion from ViaInheritMethod to
    Ensuring [ViaInheritMethod] performed by method any2Ensuring in
    scala.Predef.
* Definition Classes
  * Ensuring

(added by implicit convertion: scala.Predef.any2Ensuring#ensuring(Boolean,⇒Any):ViaInheritMethod)


 `def formatted(fmtstr: String): String`
-----------------------------------------

* Implicit information
  * This member is added by an implicit conversion from ViaInheritMethod to
    StringFormat performed by method any2stringfmt in scala.Predef.
* Definition Classes
  * StringFormat
* Annotations
  * @inline ()

(added by implicit convertion: scala.Predef.any2stringfmt#formatted(String):String)


 `val self: Any`
-----------------

* Implicit information
  * This member is added by an implicit conversion from ViaInheritMethod to
    StringAdd performed by method any2stringadd in scala.Predef.
* Shadowing
  * This implicitly inherited member is ambiguous. One or more implicitly
    inherited members have similar signatures, so calling this member may
    produce an ambiguous implicit conversion compiler error. To access this
    member you can use a
    [type ascription](http://stackoverflow.com/questions/2087250/what-is-the-purpose-of-type-ascription-in-scala)
    :
    ```scala
    (viaInheritMethod: StringAdd).self
    ```
* Definition Classes
  * StringAdd

(added by implicit convertion: scala.Predef.any2stringadd#self:Any)


 `def x: ViaInheritMethod`
---------------------------

* Implicit information
  * This member is added by an implicit conversion from ViaInheritMethod to
    ArrowAssoc [ViaInheritMethod] performed by method any2ArrowAssoc in
    scala.Predef.
* Shadowing
  * This implicitly inherited member is ambiguous. One or more implicitly
    inherited members have similar signatures, so calling this member may
    produce an ambiguous implicit conversion compiler error. To access this
    member you can use a
    [type ascription](http://stackoverflow.com/questions/2087250/what-is-the-purpose-of-type-ascription-in-scala)
    :
    ```scala
    (viaInheritMethod: ArrowAssoc[ViaInheritMethod]).x
    ```
* Definition Classes
  * ArrowAssoc
* Annotations
  * @deprecated
* Deprecated
  * _ (Since version 2.10.0) _ Use `leftOfArrow instead`

(added by implicit convertion: scala.Predef.any2ArrowAssoc#x:ViaInheritMethod)


 `def →[B](y: B): (ViaInheritMethod, B)`
-----------------------------------------

* Implicit information
  * This member is added by an implicit conversion from ViaInheritMethod to
    ArrowAssoc [ViaInheritMethod] performed by method any2ArrowAssoc in
    scala.Predef.
* Definition Classes
  * ArrowAssoc

(added by implicit convertion: scala.Predef.any2ArrowAssoc#→[B](B):(ViaInheritMethod,B))

