# com.todesking.dox.Id$$ChildValue


```scala
case class ChildValue(parentId: Id, localName: String) extends Child with Value with Product with Serializable
```


 `def changeParent(newParent: Id): ChildValue`
-----------------------------------------------

* Definition Classes
  * ChildValue → Child

(defined at com.todesking.dox.Id.ChildValue#changeParent(Id):ChildValue)


 `def childFullName(name: String): String`
-------------------------------------------

* Definition Classes
  * Value → Id

(defined at com.todesking.dox.Id.Value#childFullName(String):String)


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
  * ChildValue → Child → Id

(defined at com.todesking.dox.Id.ChildValue#localName:String)


 `val parentId: Id`
--------------------

* Definition Classes
  * ChildValue → Child

(defined at com.todesking.dox.Id.ChildValue#parentId:Id)


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

