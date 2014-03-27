//package org.remotefutures.examples
//
//import org.remotefutures.core.RemoteFuture
//import scala.concurrent.Future
//
///**
// * Created by mse on 26.03.14.
// */
//object DocumentationExample {
//  def main(args: Array[String]) : Unit = {
//
//    def lengthyComputation1 : Int = {
//      // calculate an extremly complicated Int value
//      42
//    }
//
//    val x : Future[Int] = RemoteFuture {
//      lengthyComputation1
//    }
//
//    def lengthyComputation2 : (Int => Int) = {
//      // calculate fancy stuff
//      x => x * 2
//    }
//
//    // y contains the result of lengthyComputation2( lengthyComputation1 )
//    val y: Future[Int] = x map lengthyComputation2
//
//    val y2 = RemoteFuture {
//      lengthyComputation2( lengthyComputation1 )
//    }
//  }
//}
