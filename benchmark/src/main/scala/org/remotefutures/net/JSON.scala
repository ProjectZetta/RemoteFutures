/*
 * Copyright (c) 2014 Marvin Hansen.
*/
package org.remotefutures.net

import net.minidev.json.JSONValue
import net.minidev.json.JSONArray
import net.minidev.json.JSONObject
import scala.language.dynamics

/**
  */
object JSON {

  def parseJSON(s: String) = new ScalaJSON(JSONValue.parse(s))

  // SuppressWarnings("unchecked")
  // global uncheck doesn't work
  // The scala guys believe it's better to ignore the issue....sigh
  //https://issues.scala-lang.org/browse/SI-1781
  def makeJSON(a: Any): String = a match {
    case m: Map[String, Any]@unchecked => m.map {
      case (name, content) => "\"" + name + "\":" + makeJSON(content)
    }.mkString("{", ",", "}")
    case l: List[Any] => l.map(makeJSON).mkString("[", ",", "]")
    //case l: java.util.List[Any] => l.map(makeJSON).mkString("[", ",", "]")
    case s: String => "\"" + s + "\""
    case i: Int => i.toString
  }

}

class JSONException extends RuntimeException

class ScalaJSONIterator(i: java.util.Iterator[java.lang.Object]) extends Iterator[ScalaJSON] {
  def hasNext = i.hasNext

  def next() = new ScalaJSON(i.next())
}

class ScalaJSON(o: java.lang.Object) extends Seq[ScalaJSON] with Dynamic {
  override def toString(): String = o.toString

  def toInt: Int = o match {
    case i: Integer => i
    case _ => throw new JSONException
  }

  def toDouble: Double = o match {
    case d: java.lang.Double => d
    case f: java.lang.Float => f.toDouble
    case _ => throw new JSONException
  }

  def apply(key: String): ScalaJSON = o match {
    case m: JSONObject => new ScalaJSON(m.get(key))
    case _ => throw new JSONException
  }

  def apply(idx: Int): ScalaJSON = o match {
    case a: JSONArray => new ScalaJSON(a.get(idx))
    case _ => throw new JSONException
  }

  def length: Int = o match {
    case a: JSONArray => a.size()
    case m: JSONObject => m.size()
    case _ => throw new JSONException
  }

  def iterator: Iterator[ScalaJSON] = o match {
    case a: JSONArray => new ScalaJSONIterator(a.iterator())
    case _ => throw new JSONException
  }

  def selectDynamic(name: String): ScalaJSON = apply(name)

  def applyDynamic(name: String)(arg: Any) = {
    arg match {
      case s: String => apply(name)(s)
      case n: Int => apply(name)(n)
      case u: Unit => apply(name)
    }
  }
}

