# Concept - RemoteFuture as a Monad

A monad is a functor plus methods _unit_ and _flatMap_ on that structure.

## Current state

Given the code

    def lengthyComputation1 : Int = {
      // calculate an extremly complicated Int value
      42
    }

    val x : Future[Int] = RemoteFuture {
      lengthyComputation1
    }

this will return a regular future _x_, but the computation ( lengthyComputation1 to calculate an Int) is performed on some remote node.


If we now want to combine remote computations (f.e. via the Monad _map()_ operation), we can simply do

    def lengthyComputation2 : (Int => Int) = {
      // calculate fancy stuff
      x => x * 2
    }

    // y contains the result of lengthyComputation2( lengthyComputation1 )
        val y: Future[Int] = x map lengthyComputation2

to combine the computations. Really? No. As the map operation is the map operation of Future and not RemoteFuture, the operation lengthyComputation2_ is just executed locally and not remotely.

Remark: We could have written to execute the concatenation of _lengthyComputation2_ and _lengthyComputation1_, but that is cumbersome and does **not** allow the lifting of functions.

    val y2 = RemoteFuture {
      lengthyComputation2( lengthyComputation1 )
    }

Instead, we want to combine the computation _lengthyComputation1_ and _lengthyComputation2_, such that even their concatenation is executed remotely.

*Editorial note: A function is itself a functor.*

## Monad

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

## Proposal / ideas

    trait RemoteFuture[+T] {

      def map[S]( f: T => S ) : RemoteFuture[S] = {
        val p = Promise[S]()
        onComplete { v => p complete (v map f) }
        p.future
      }

      def flatMap[S](f: T => RemoteFuture[S]): RemoteFuture[S]

       /**
         * When this remote future is completed on caller site, either through an exception, or a value,
         * apply the provided function.
         */
      def onLocalComplete[U]( f: Try[T] => U)(executor: RemoteExecutionContext): Unit

       /**
         * When this remote future is completed on remote site, either through an exception, or a value,
         * apply the provided function.
         */
      def onRemoteComplete[U]( f: Try[T] => U)(executor: RemoteExecutionContext): Unit

    }



    val x : RemoteFuture[Int] = RemoteFuture {
      computeSomething
    }






