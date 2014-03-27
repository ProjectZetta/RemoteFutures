# Concept - RemoteFuture

A monad M is a functor plus the additional operations _unit_ and _flatMap_ on that structure.
A functor allows the lifting of a function A=>B into the context of the monad, thus M[A] => M[B].
The method _unit_ allows for the creation of a monad from a value, thus puts a value into the context of the monad.

    unit(a:A) : M[A]

The _flatMap_ operation applies a function A=>M[B] to the

## Current state

In the current implementation a RemoteFuture executes some function body on a remote node (with load balancing, etc.) and returns the result as a future.

Given the code

    def lengthyComputation1 : Int = {
      // calculate an extremly complicated Int value
      42
    }

    val x : Future[Int] = RemoteFuture {
      lengthyComputation1
    }

this will return a regular future _x_, but the computation ( lengthyComputation1 to calculate an Int) is performed on some remote node.

# Case studies

## Case 1: Concatenation of asynchronous results

If we now want to combine remote computations (f.e. via the Monad _map()_ operation), we can simply do

*Editorial remark: Precisely, _map()_ is an operation on an Applicative, which itself extends Monad (e.g. see Scalaz)*

    def lengthyComputation2 : (Int => Int) = {
      // calculate fancy stuff
      x => x * 2
    }

    // y contains the result of lengthyComputation2( lengthyComputation1 )
    val y: Future[Int] = x map lengthyComputation2

to combine the computations. Really? No. As the map operation is the map operation of Future and not RemoteFuture, the operation lengthyComputation2_ is just executed locally and not remotely.

Remark: We could have written to execute the concatenation of _lengthyComputation2_ and _lengthyComputation1_, but that is cumbersome and does **not** allow the lifting of functions.

    val y = RemoteFuture {
      lengthyComputation2( lengthyComputation1 )
    }

*Editorial note: A function is itself a functor.*

Instead, we want to combine the computation _lengthyComputation1_ and _lengthyComputation2_, such that even their concatenation is executed remotely. For the sake of simplicity let us assume we
already have a new type RemoteFuture and we can write down the desired behavior as

    val x : RemoteFuture[Int] = RemoteFuture { lengthyComputation1 }
    val y_r : RemoteFuture[Int] = x map lengthyComputation2
    val y : Future[Int] = y_r makeLocal

Here, we have used a method called makeLocal, which turns a RemoteFuture[T] into a Future[T]

## Case 2: Combination of future results

Let us regard a different case: The combination of two results from asynchronous remote computations. In the first step, just focus on the regular local case.
This is shown in the following excerpt

    val f1 : Future[Int] = Future { heavyComputationOfFirstFactor }
    val f2 : Future[Int] = Future { heavyComputationOfSecondFactor }
    val fMultiplication: Future[Int] = {
      for {
        v1 <- f1
        v2 <- f2
      } yield v1*v2
    }

With the help of the translation scheme for for-comprehensions,

     for (x <- expr1) yield expr2                     ==> expr1.map(x => expr2)

     for (x <- expr1; y <- expr2) yield expr3         ==> expr1.flatMap(x => for (y <- expr2) yield expr3) and then
                                                          expr1.flatmap(x => expr2.map(y => expr3))

the above expression can be translated into

     for (x <- expr1; y <- expr2) yield expr3         ==> f1.flatmap( v1 => f2.map( v2 => v1*v2 ))

Ok. Now we try to *simply* translate this to monad RemoteFuture

    val f1 : RemoteFuture[Int] = RemoteFuture { heavyComputationOfFirstFactor }
    val f2 : RemoteFuture[Int] = RemoteFuture { heavyComputationOfSecondFactor }
    val fMultiplication: RemoteFuture[Int] = {
      for {
        v1 <- f1
        v2 <- f2
      } yield v1*v2
    }

## Consequences of the use-cases above

### Solution 1:

- Operation RemoteFuture.apply (written as RemoteFuture {} ) creates a new remote future, running on an arbitrary node. The node selection for remote execution depends on the strategy.
- Subsequent operations (like map / flatMap) will always be executed on **exactly** the same node.

### Solution 2:

- Operation RemoteFuture.apply( => body ) and all function like map, flatMap and a like are "equipped" with an (location: ExecutionLocalion) argument.
  Thus, it can be spefied, where the remote future is executed. The localtion of execution can be
    - retrieved from another remote future via _def sameLocationAs: ExecutionLocation
    - specified by ExecutionLocation.ByStrategy
    - specified with ExecutionLocation.Local


# Proposal

## RemoteFuture Monad

Now, we assume a monad RemoteFuture, such that we can write

    val x : RemoteFuture[Int] = RemoteFuture {
      // lengthyComputation1: calculate an Int value
      42
    }

Notice the return type is RemoteFuture and not Future as beforehand.

Next, if we add the _map_ method in the usual definition for a monad

    def map[S]( f: T => S ) : RemoteFuture[S]

to the RemoteFuture trait, we can concatenate remote executions, so that the computations are executed on the some node. Here, this refers to the mentioned lifting of the function

    f: T=>S

to the monad RemoteFuture. The lifted function is then

    RemoteFuture[T] => RemoteFuture[S]

So what should this _map_ method do? Well, it should again execute the mapping operation on a remote node.

*Editorial note: Unclear right now is, which remote node? The same node? Or a distinct remote node? How large is the performance penalty? How is data transferred then?*



### Idea of monad RemoteFuture

    trait RemoteFuture[+T] {

      def map[S]( f: T => S )(location: ExecutionLocation) : RemoteFuture[S]

      def flatMap[S](f: T => RemoteFuture[S])(location: ExecutionLocation): RemoteFuture[S]

      /**
        * When this remote future is completed on caller site, either through an exception, or a value,
        * apply the provided function.
        *
        * (executor: RemoteExecutionContext) was changed to (location: ExecutionLocation)
        */
      def onComplete[U]( f: Try[T] => U)(location: ExecutionLocation): Unit

      /**
        * Turn this RemoteFuture[T] into a Future[T]
        */
      def makeLocal : Future[T]
    }
