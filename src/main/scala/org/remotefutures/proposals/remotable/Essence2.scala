package org.remotefutures.proposals.remotable

import scala.concurrent.{ExecutionContext, Future}

class Essence2 {
  // Remote( Future (body) ) => RemoteFuture( body )
  // Remote( body ) => Remote( Future( body )).await
  // Remote( Future (body).map ) ......map the result remotely
  // Remote( Future (body) ).map ..... map the result locally
  // ===> return type of Remote( ... ) is Future
  // ===> make sure, body does not capture unstable values
  // ===> body is ( () => T )

  // e => Future ( s(e) )
  // es.map( e => Future( s(e) )

  // es.map( e => RemoteFuture( s(e) ) )       ..... e and context of s needs to be transferred
  // es.map( e, f => RemoteFuture (s(e, f) )   ..... e, f and context of s need to be transferred
  // ....
  // es.map( e => Remote ( Future ( s(e) ) )
}

class FunctionEssence {
  // what about a remote function ?

  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  val es = List("a", "bb", "ccc")

  val fs = es.map( e => e.length )

  trait RemoteFunction1[T, R] extends Function1[T, R] {
  }

  val f : (String => Int) = {
    x => x.length
  }

  val g : (Double => String) = {
    y => y.toString
  }

  // f @ g = f ( g ( arg ) )
  // val h: (Double) => Int = f .compose( g )

  // Remote[String]
  //   with map ( String => Int ) ===> Remote[Int] -> Remote[String]

  val x = Future{ 3 }

  val fnc1 : (Int, String) => Double = {
    ( x, y ) => (x + y.length).toDouble
  }

  val x2: Future[(String) => Double] = x.map(fnc1.curried)


//  val r: Future[(String) => Double] = for {
//    y <- f
//  } yield fnc1.curried(y)
}
