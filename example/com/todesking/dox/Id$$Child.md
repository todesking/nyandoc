# com.todesking.dox.Id$$Child


```scala
sealed abstract class Child extends Id
```


 abstract def changeParent(parent: Id): Child
----------------------------------------------

(defined at com.todesking.dox.Id.Child#changeParent(Id):Child)


 abstract def childFullName(name: String): String
--------------------------------------------------

* Definition Classes
  * Id

(defined at com.todesking.dox.Id#childFullName(String):String)


 val fullName: String
----------------------

* Definition Classes
  * Child → Id

(defined at com.todesking.dox.Id.Child#fullName:String)


 def isAncestorOf(id: Id): Boolean
-----------------------------------

* Definition Classes
  * Child → Id

(defined at com.todesking.dox.Id.Child#isAncestorOf(Id):Boolean)


 def isParentOf(id: Id): Boolean
---------------------------------

* Definition Classes
  * Child → Id

(defined at com.todesking.dox.Id.Child#isParentOf(Id):Boolean)


 abstract val localName: String
--------------------------------

* Definition Classes
  * Child → Id

(defined at com.todesking.dox.Id.Child#localName:String)


 abstract val parentId: Id
---------------------------

(defined at com.todesking.dox.Id.Child#parentId:Id)


 def relNameFrom(base: Id): String
-----------------------------------

* Definition Classes
  * Child → Id

(defined at com.todesking.dox.Id.Child#relNameFrom(Id):String)


 def toString(): String
------------------------

* Definition Classes
  * Id → AnyRef → Any

(defined at com.todesking.dox.Id#toString():String)

