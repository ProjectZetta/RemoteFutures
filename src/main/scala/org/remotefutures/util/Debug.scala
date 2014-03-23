package org.remotefutures.util

object Debug {

  def printDbg(msg: String)(implicit DBG: Boolean): Unit = {
    if (DBG) println(msg)
  }

}

