package org.remotefutures.proposals.remotable

import org.remotefutures.spores._

object SporeExamples {
  case class Person(name: String, age: Int)
//
//  def foo: Unit = {
//
//    val outer2 = Person("Jim", 35)
//    val s = spore {
//      // sequence of local value declarations
//      val outer1 = 0
//      val inner = outer2
//      val age2 = inner.age
//      // closure
//      (x: Int) => {
//        s"The result is: ${x}"
//        // s"The result is: ${inner.age}"
//        // s"The result is: ${x + inner.age + outer1}"
//        s"The result is: ${age2}"
//        // s"The result is: ${x.+(age2)}" // => not working as " (symbol) invalid reference to method + "
//      }
//    }
//  }
}
