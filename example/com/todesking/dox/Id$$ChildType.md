# com.todesking.dox.Id$$ChildType


```scala
sealed case class ChildType(parentId: Id, localName: String) extends Child with Type with Product with Serializable
```


 `def changeParent(newParent: Id): ChildType`
----------------------------------------------

* Definition Classes
  * ChildType → Child

(defined at com.todesking.dox.Id.ChildType#changeParent(Id):ChildType)


 `def childFullName(name: String): String`
-------------------------------------------

* Definition Classes
  * Type → Id

(defined at com.todesking.dox.Id.Type#childFullName(String):String)


 `val fullName: String`
------------------------

* Definition Classes
  * Child → Id

(defined at com.todesking.dox.Id.Child#fullName:String)


 `def isAncestorOf(id: Id): Boolean`
-------------------------------------

* Definition Classes
  * Child → Id

(defined at com.todesking.dox.Id.Child#isAncestorOf(Id):Boolean)


 `def isParentOf(id: Id): Boolean`
-----------------------------------

* Definition Classes
  * Child → Id

(defined at com.todesking.dox.Id.Child#isParentOf(Id):Boolean)


 `val localName: String`
-------------------------

* Definition Classes
  * ChildType → Child → Id

(defined at com.todesking.dox.Id.ChildType#localName:String)


 `val parentId: Id`
--------------------

* Definition Classes
  * ChildType → Child

(defined at com.todesking.dox.Id.ChildType#parentId:Id)


 `def relNameFrom(base: Id): String`
-------------------------------------

* Definition Classes
  * Child → Id

(defined at com.todesking.dox.Id.Child#relNameFrom(Id):String)


 `def toString(): String`
--------------------------

* Definition Classes
  * Id → AnyRef → Any

(defined at com.todesking.dox.Id#toString():String)

