package org.remotefutures.proposals.remotable

/**
 * Created by martin on 29.12.14.
 */
object TestCalls {

  def main(args: Array[String]) : Unit = {
    // val f : (String => Int) = ( x => x.length )

    val x = List("asdf", "ab", "c")

    // val result: List[BigInt] = x.map( e => Foo( () => BigInt(e.length) ) )

    val f1 = { e:String => Foo( () => BigInt(e.length) ) }
//
//    val f1_prime = new Function1[String, BigInt] {
//      override def apply(e: String): BigInt = {
//        Foo( () => BigInt(e.length) )
//      }
//    }

    val result: List[BigInt] = x.map( f1  )
  }
}

object Foo {
  def apply[T]( body: () => T ) : T = {
    println("Foo apply() is called.")
    body()
  }
}
