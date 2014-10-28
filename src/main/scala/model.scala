package com.todesking.dox

import LibGlobal._

sealed trait Id {
  def isParentOf(id:Id):Boolean
  def isAncestorOf(id:Id):Boolean
  def fullName():String
  def localName():String
  def childFullName(name:String):String
  def relNameFrom(base:Id):String
  override def toString = fullName
}

object Id {
  sealed abstract class Root extends Id {
    override def fullName = ""
    override def localName = ""
    override def isParentOf(id:Id):Boolean = id match {
      case child:Child => child.parentId == this
      case _:Root => false
    }
    override def isAncestorOf(id:Id):Boolean = id match {
      case _:Root => false
      case _ => true
    }
    override def childFullName(name:String) = name
    override def relNameFrom(base:Id):String = base match {
      case _:Root => fullName
      case _:Child => throw new IllegalArgumentException()
    }
  }
  sealed abstract class Child extends Id {
    val parentId:Id
    override val localName:String

    def changeParent(parent:Id):Child

    override val fullName =
      parentId.childFullName(localName)
    override def isParentOf(id:Id):Boolean = id match {
      case child:Child => child.parentId == this
      case _:Root => false
    }
    override def isAncestorOf(id:Id):Boolean =
      id match {
        case child:Child =>
          isParentOf(child) || isAncestorOf(child.parentId)
        case _:Root => false
      }
    override def relNameFrom(base:Id):String = {
      require(base.isAncestorOf(this))
      base match {
        case _:Root => fullName
        case _:Child => fullName.substring(base.fullName.length)
      }
    }
  }

  sealed trait Type extends Id {
    override def childFullName(name:String) =
      fullName + "#" + name
  }
  sealed trait Value extends Id {
    override def childFullName(name:String) =
      fullName + "." + name
  }

  // theres no RootTypeId
  object Root extends Root with Value {
    override def childFullName(name:String) = super[Root].childFullName(name)
  }

  case class ChildValue(
    override val parentId:Id,
    override val localName:String
  ) extends Child with Value {
    override def changeParent(newParent:Id) =
      ChildValue(newParent, localName)
  }

  sealed case class ChildMethod(
    override val parentId:Id,
    val localBaseName:String,
    val paramId:String
  ) extends Child with Value {
    override val localName = localBaseName + paramId
    override def changeParent(newParent:Id) =
      ChildValue(newParent, localName)
  }

  sealed case class ChildType(
    override val parentId:Id,
    override val localName:String
  ) extends Child with Type {
    override def changeParent(newParent:Id) =
      ChildType(newParent, localName)
  }

  def Type(fullName:String):ChildType =
    build(fullName)(ChildType.apply)
  def Value(fullName:String):ChildValue =
    build(fullName)(ChildValue.apply)

  private def build[A <: Id](fullName:String)(builder:(Id, String) => A):A = {
    // FIXME: IT IS VERY BAD but I have no idea how to fix it
    if(fullName.endsWith("###")) {
      val parent = Type(fullName.substring(0, fullName.length - 3))
      builder(parent, "##")
    } else {
      """([.#])([^.#]+)$""".r.findFirstMatchIn(fullName) match {
        case Some(m) =>
          if(m.group(1) == ".") builder(Value(m.before.toString), m.group(2))
          else builder(Type(m.before.toString), m.group(2))
        case None =>
          builder(Root, fullName.toString)
      }
    }
  }
}

sealed class ItemKind
object ItemKind {
  object Type extends ItemKind
  object Value extends ItemKind
}

sealed class TypeKind(val signature:String)
object TypeKind {
  case object Trait extends TypeKind("trait")
  case object Class extends TypeKind("class")
  case object CaseClass extends TypeKind("case class")
  case object Type extends TypeKind("type")

  def forName(name:String):TypeKind = name match {
    case "trait" => Trait
    case "class" => Class
    case "case class" => Class
    case "type" => Type
    case unk => errorUnknown("TypeKind", unk)
  }
}

sealed abstract class ValueKind
object ValueKind {
  case object Val extends ValueKind
  case object Var extends ValueKind
  case object Object extends ValueKind
  case object Def extends ValueKind
  case object Package extends ValueKind

  def forName(name:String):ValueKind = {
    name match {
      case "val" | "lazy val" => Val
      case "var" => Var
      case "object" => Object
      case "def" => Def
      case "package" => Package
      case unk => errorUnknown("ValueKind", unk)
    }
  }
}

sealed class Scope
object Scope {
  case class Private() extends Scope
  case class Protected() extends Scope
  case class Public() extends Scope
}

case class MethodParams(signature:String)
case class ResultType(signature:String)

sealed abstract class Item(val id:Id, val comment:Seq[Markup]) {
  def signature:String = id.localName
}

sealed class Value(id:Id.Value, comment:Seq[Markup]) extends Item(id, comment)

case class Type(
  override val id:Id.Type,
  kind:TypeKind,
  override val signature:String,
  override val comment:Seq[Markup]
) extends Item(id, comment)

case class Object(
  override val id:Id.Value,
  override val signature:String,
  override val comment:Seq[Markup]
) extends Value(id, comment)

abstract class Method(
  id:Id.Value,
  params:MethodParams,
  resultType:ResultType,
  override val signature:String,
  comment:Seq[Markup]
) extends Value(id, comment)

case class DefinedMethod(
  override val id:Id.Value,
  params:MethodParams,
  resultType:ResultType,
  override val signature:String,
  override val comment:Seq[Markup]
) extends Method(id, params, resultType, signature, comment)

case class ViaImplicitMethod(
  override val id:Id.Value,
  params:MethodParams,
  resultType:ResultType,
  override val signature:String,
  originalId:Id.Value,
  override val comment:Seq[Markup]
) extends Method(id, params, resultType, signature, comment)

case class ViaInheritMethod(
  override val id:Id.Value,
  params:MethodParams,
  resultType:ResultType,
  override val signature:String,
  originalId:Id.Value,
  override val comment:Seq[Markup]
) extends Method(id, params, resultType, signature, comment)

case class Package(override val id:Id.Value, override val comment:Seq[Markup]) extends Value(id, comment)

trait Repository {
  def isTopLevel(item:Item) = {
    parentOf(item) match {
      case Some(_:Package) => true
      case Some(_) => false
      case None => true
    }
  }

  def topLevelItems():Seq[Item]
  def parentOf(item:Item):Option[Item]
  def childrenOf(item:Item):Seq[Item]
}

object Repository {
  def apply(items:Seq[(Item, Seq[Item])]):Repository = {
    import scala.collection.mutable.{HashMap, Set, MultiMap}
    val id2item = new HashMap[Id, Item]
    for {
      (item, subItems) <- items
    } {
      id2item.put(item.id, item)
      subItems.foreach {sub =>
        if(!id2item.contains(sub.id))
          id2item.put(sub.id, sub)
      }
    }
    new Repository {
      override def topLevelItems():Seq[Item] = {
        id2item.values.filter(isTopLevel(_)).toSeq
      }
      override def parentOf(item:Item):Option[Item] =
        item.id match {
          case Id.Root => None
          case child:Id.Child => id2item.get(child.parentId)
        }
      override def childrenOf(item:Item):Seq[Item] =
        id2item.filter {case (id, _) => item.id.isParentOf(id) }.map(_._2).toSeq
    }
  }
}

