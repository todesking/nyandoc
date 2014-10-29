# com.todesking.dox.Id


```scala
sealed trait Id extends AnyRef
```


 `abstract def childFullName(name: String): String`
----------------------------------------------------



 `abstract def fullName(): String`
-----------------------------------



 `abstract def isAncestorOf(id: Id): Boolean`
----------------------------------------------



 `abstract def isParentOf(id: Id): Boolean`
--------------------------------------------



 `abstract def localName(): String`
------------------------------------



 `abstract def relNameFrom(base: Id): String`
----------------------------------------------



 `def toString(): String`
--------------------------

* Definition Classes
  * Id → AnyRef → Any


