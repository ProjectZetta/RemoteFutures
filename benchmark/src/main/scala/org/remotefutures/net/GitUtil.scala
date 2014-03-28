/*
 * Copyright (c) 2014 Marvin Hansen.
*/
package org.remotefutures.net

import java.util.concurrent.atomic.AtomicInteger
import scala.collection.immutable.TreeMap
import org.remotefutures.net.GithubJsonCache.cache


/**
 * @author Marvin Hansen
 *
 *         Companion object for static access
 */
object gitUtil extends GitUtil {

  def apply = new GitUtil
}

/**
 * trait for mix-in
 */
trait GitUtils extends GitUtil


/**
 * Create a new instance for each repo
 * to maintain different file counters
 */
class GitUtil {

  private final val t: String = "type"
  private final val uri: String = "url"
  private final val nme: String = "name"
  private final val i: AtomicInteger = new AtomicInteger(1)
  private final val cnt: AtomicInteger = new AtomicInteger(1)

  private final val sb = new StringBuilder
  private final val off: String = "  "


  /**
   * print the content of a remote git repository
   *
   * @param depth  traversal depth of files
   * @param url Repo URL to fetch
   */
  def printContent(depth: Int)(implicit url: String): Unit = {

    val tree = cache.getJSONfromURL(url)

    printFolder(tree, depth)

  }

  /**
   * @param tree JSON Tree to print
   */
  private def printFolder(tree: ScalaJSON, depth: Int): Unit = {
    val MN = "getFolders"

    // make the blank longer for each level of depth
    sb.append(off)
    val o = sb.toString()

    for (e <- tree) {
      val tpe = getStringElem(e, t)
      if (tpe.equals("dir")) {

        val name = getStringElem(e, nme)
        val url = getStringElem(e, uri)
        val subFolder = cache.getJSONfromURL(url)

        println(o + name + "/")

        if (depth > 1) {

          //print all files folders first if depth isn't reached yet
          if (i.get() != depth) {
            printFolder(subFolder, depth)
            i.incrementAndGet()
          }
          else {
            // otherwise, print only name of all subfolders
            for (e <- subFolder) {
              val tpe = getStringElem(e, t)
              if (tpe.equals("dir")) {
                val subFolderName = getStringElem(e, nme)
                println(o + subFolderName + "/")
              }
            }
          }

        }
      }
    }
    // print files below folder
    printFiles(o, tree)
  }

  /**
   * Prints all files from a JSON tree sorted by size
   *
   * @param tree  JSON tree / element
   */
  private def printFiles(off: String, tree: ScalaJSON): Unit = {

    val MN = "printFiles"
    // reverse ordered Int tree map: 5.4.3.2...
    var map = new TreeMap[Int, String]()(Ordering[Int].reverse)

    for (e <- tree) {
      val tpe = getStringElem(e, t)
      if (tpe.equals("file")) {
        val name = getStringElem(e, nme)
        val size = e.selectDynamic("size").toInt

        map += (size -> name)
      }
    }
    for (e <- map) {
      println(off + cnt.getAndIncrement + ". " + e._2)
    }
  }


  /**
   *
   * Helper for extracting string values from a nested JSON element or tree
   *
   * Be careful, this boy is dynamically typed thus if the target value isn't
   * a String or cannot be cast into a String, it crashes....
   *
   * ....if that's the case, write another getElem to cast into your
   * specific type.
   *
   * @param e JSON Tree / Element
   * @param name name of the field to extract
   * @return String value of the field
   */
  private def getStringElem(e: ScalaJSON, name: String): String = {
    e.selectDynamic(name).toString()
  }

  private def getIntElem(e: ScalaJSON, name: String): Int = {
    e.selectDynamic(name).toInt
  }

  private def getDoubleElem(e: ScalaJSON, name: String): Double = {
    e.selectDynamic(name).toDouble
  }

}
