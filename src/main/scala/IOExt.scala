package com.todesking.dox

object IOExt {
  import java.io._
  implicit class InputStreamExt[A <: InputStream](self:A) {
    def readAll():String = {
      val sb = new StringBuilder
      val reader = new InputStreamReader(self)
      val bufSize = 10240
      val buf = new Array[Char](bufSize)

      var readSize:Int = 0
      while({readSize = reader.read(buf, 0, bufSize); readSize > 0})
        sb.appendAll(buf, 0, readSize)

      sb.toString
    }
  }
}
