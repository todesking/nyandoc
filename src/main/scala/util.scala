package com.todesking.nyandoc

object LibGlobal {
  def errorUnknown(name:String, unk:String) =
    throw new RuntimeException(s"Unknown ${name}: ${unk}")
  def unsupportedFeature(name:String, desc:String):Unit =
    println(s"WARN: [${name}] ${desc} is not supported yet")
}

