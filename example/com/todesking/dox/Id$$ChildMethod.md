# com.todesking.dox.Id$$ChildMethod


```scala
sealed case class ChildMethod(parentId: Id, localBaseName: String, paramId: String) extends Child with Value with Product with Serializable
```


 def changeParent(newParent: Id): ChildValue
---------------------------------------------

* Definition Classes
  * ChildMethod → Child

(defined at com.todesking.dox.Id.ChildMethod#changeParent(Id):ChildValue)


 def childFullName(name: String): String
-----------------------------------------

* Definition Classes
  * Value → Id

(defined at com.todesking.dox.Id.Value#childFullName(String):String)


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


 val localBaseName: String
---------------------------

(defined at com.todesking.dox.Id.ChildMethod#localBaseName:String)


 val localName: String
-----------------------

* Definition Classes
  * ChildMethod → Child → Id

(defined at com.todesking.dox.Id.ChildMethod#localName:String)


 val paramId: String
---------------------

(defined at com.todesking.dox.Id.ChildMethod#paramId:String)


 val parentId: Id
------------------

* Definition Classes
  * ChildMethod → Child

(defined at com.todesking.dox.Id.ChildMethod#parentId:Id)


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

