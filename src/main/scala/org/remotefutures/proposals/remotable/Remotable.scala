//package org.remotefutures.proposals.remotable
//
//import org.remotefutures.core.{impl, RemoteExecutionContext}
//import org.remotefutures.spores._
//
//import scala.concurrent.{ExecutionContext, Promise, Future}
//
///**
// * Created by mse on 12.11.14.
// */
//object Remote {
//  // type Remotable[T] = Latent[NullarySpore[T]]
//
//  def apply[T](r: Latent[NullarySpore[T]]) : Future[T] = {
//    null
//  }
//
//  def apply[T](r: Future[NullarySpore[T]]) : Future[T] = {
//    null
//  }
//
//  def apply[T](spore: NullarySpore[T]): Future[T] = ???
//}
//
//object Foo {
//
//  import scala.concurrent.ExecutionContext.Implicits.global
//
//  def bar1() : Unit = {
//    val x = Future( "asdf" )
//    val y = x.map(x => x.length) // executed locally
//  }
//
//  def bar2() : Unit = {
//    val x: Future[Int] = Remote {
//      Latent {
//        spore {
//          () => "asdf"
//        }
//      }
//    }.map( x => x.length )
//  }
//
//  def bar3() : Unit = {
//    val z = Latent {"asdf"}
//
//    val x = Remote {
//      val l = Latent {
//        spore {
//          () => "asdf"
//        }
//      }
//      // .map(x => x.length)
//      l
//    }
//  }
//
//
//object RemoteFuture2 {
//  /**
//   * execute body on remote node in sync
//   */
//   def apply[T]( body: => T ) : Future[T] = ???
//
//}
//
//object Remote2 {
//
//
//  /**
//   * execute body on remote node async
//   */
//  def apply[T]( async: => Future[T] ) : Future[T] = {
//    // transfer async
//    val p = Promise[T]()
//    async.onComplete {
//      x => p.complete(x)
//    }
//    p.future
//  }
//}
//
//object Foo2 {
//  def foo() : Unit = {
//    val r: Future[Int] = RemoteFuture2 {
//       // "asdf".map( x => x.length )
//      5
//    }
//
//  }
//  def foo2() : Unit = {
//    val r = RemoteFuture2 {
//      "asdf"
//    }.map( x => x.length )
//
//  }
//}
//
//
//
//
////
////  Remote {
////    Latent {"asdf"}.map( x => x.length)
////  }
////
////  Remote { // RemoteFuture
////    "asdf".map( x => x.length )
////  }
////
////  Remote {
////
////  }
//
//
////  implicit def latentToFuture[T]( latent: Latent[NullarySpore[T]]) : Future[T] = {
////    latent.future
////  }
//
////  implicit def sporeToRegular[T]( r : Latent[NullarySpore[T]] ) : T = ???
//
//}
//
//
//
//
//object Latent {
//  def apply[T](body: => T) = new Latent(body)
//
//  // def apply[T](spore: NullarySpore[T]) = new Latent(spore)
//  def apply[T](spore: NullarySpore[T]) = new Latent(spore)
//}
//
//class Latent[T](f: => T)  {
//  private val p = Promise[T]()
//
//  def trigger() {
//    p.success(f)
//  }
//
//  def future: Future[T] = p.future
//
//  def map[S](f: T => S)(implicit executor: ExecutionContext): Future[S] =  { // transform(f, identity)
//    val p = Promise[S]()
//    // onComplete { v => p complete (v map f) }
//    p.future
//  }
//}
//
//
//class Latent_orig[T](f: => T) {
//  private val p = Promise[T]()
//
//  def trigger() {
//    p.success(f)
//  }
//
//  def future: Future[T] = p.future
//}
//
//object Latent_orig {
//  def apply[T](f: => T) = new Latent_orig(f)
//}
//
////
////// ==============
////object Remotable {
////  def apply[T](spore: NullarySpore[T])
////}
////
////trait Location {
////
////}
////
////object Local extends Location {
////
////}