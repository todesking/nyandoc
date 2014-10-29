# com.todesking.dox.Id$$Type


```scala
sealed trait Type extends Id
```


 def childFullName(name: String): String
-----------------------------------------

* Definition Classes
  * Type → Id

(defined at com.todesking.dox.Id.Type#childFullName(String):String)


 abstract def fullName(): String
---------------------------------

* Definition Classes
  * Id

(defined at com.todesking.dox.Id#fullName():String)


 abstract def isAncestorOf(id: Id): Boolean
--------------------------------------------

* Definition Classes
  * Id

(defined at com.todesking.dox.Id#isAncestorOf(Id):Boolean)


 abstract def isParentOf(id: Id): Boolean
------------------------------------------

* Definition Classes
  * Id

(defined at com.todesking.dox.Id#isParentOf(Id):Boolean)


 abstract def localName(): String
----------------------------------

* Definition Classes
  * Id

(defined at com.todesking.dox.Id#localName():String)


 abstract def relNameFrom(base: Id): String
--------------------------------------------

* Definition Classes
  * Id

(defined at com.todesking.dox.Id#relNameFrom(Id):String)


 def toString(): String
------------------------

* Definition Classes
  * Id → AnyRef → Any

(defined at com.todesking.dox.Id#toString():String)

