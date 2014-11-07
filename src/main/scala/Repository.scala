package com.todesking.dox

import LibGlobal._

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
  def childrenWithCategory(item:Item):Seq[(Item, String)]
  def allItems():Seq[Item]
}

object Repository {
  def apply(items:Seq[HtmlParser.Result]):Repository = {
    import scala.collection.mutable.{HashMap, Set, MultiMap}
    val id2item = new HashMap[Id, Item]
    val id2categoryName = new HashMap[Id, String]
    for {
      result <- items
    } {
      id2item.put(result.topItem.id, result.topItem)
      result.members.foreach {case (categoryName, subItem) =>
        if(!id2item.contains(subItem.id))
          id2item.put(subItem.id, subItem)
        id2categoryName.put(subItem.id, categoryName)
      }
    }
    val id2children = new HashMap[Id, Set[Id]] with MultiMap[Id, Id]
    def registerId(id:Id):Unit = id match {
      case _:Id.Root =>
        // do nothing
      case cid:Id.Child =>
        id2children.addBinding(cid.parentId, id)
        registerId(cid.parentId)
    }
    id2item.keys.foreach {id => registerId(id)}
    new Repository {
      override def allItems():Seq[Item] = {
        id2item.values.toSeq
      }
      override def topLevelItems():Seq[Item] = {
        id2item.values.filter(isTopLevel(_)).toSeq
      }
      override def parentOf(item:Item):Option[Item] =
        item.id match {
          case Id.Root => None
          case child:Id.Child => id2item.get(child.parentId)
        }
      override def childrenOf(item:Item):Seq[Item] =
        id2children.get(item.id) map { cids =>
          cids.flatMap {cid => id2item.get(cid)}.toSeq
        } getOrElse Seq()
      override def childrenWithCategory(item:Item):Seq[(Item, String)] =
        childrenOf(item).map {c => c -> id2categoryName.get(c.id).getOrElse("")}
    }
  }
}

