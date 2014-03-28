/*
 * Copyright (c) 2014 Marvin Hansen.
*/
package org.remotefutures.net

import JSON._
import java.util.concurrent.ConcurrentHashMap

/**
 * @author Marvin Hansen
 */


object GithubJsonCache extends GithubJsonCache

class GithubJsonCache {

  // developer.github.com/v3/#authentication
  private final val token = "dcc283cec4f69de4167db1ab06a9bfd64071a73b"
  private final val url = "https://api.github.com/"
  val cache = new JsonCache(url, token)
}


object JsonCache

class JsonCache(url: String, token: String) {

  private final val size = 50
  // use URL as key
  private final val cache = new ConcurrentHashMap[String, ScalaJSON](size)

  /**
   * @param uri Source URL
   * @return converted JSON from the URL
   */
  def getJSONfromURL(uri: String): ScalaJSON = {

    val acc = "?access_token="
    // replacing reference is required because the token ?ref conflicts with fetching commits
    // from a certain branch i.e. master. So far, no other work-around :-(
    val url = uri.replace("?ref=master", "") + acc + token

    if (cache.containsKey(url)) {
      cache.get(url)
    } else {
      val tmp = io.Source.fromURL(url).mkString
      // convert String to JSON and put converted JSON into cache to save bandwidth and computation
      val json: ScalaJSON = parseJSON(tmp)
      cache.putIfAbsent(url, json)
      json
    }
  }
}