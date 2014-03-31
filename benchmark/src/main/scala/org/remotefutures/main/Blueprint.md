# Blueprint: How to write your own implementation

##About

This document showcases the actual steps required to extend
the CBR benchmark with a custom implementation.

##Overview:

There are two strategies in terms of benchmarking:

A) Global task distribution
B) Sub-Task execution

A) Global task execution means, a handler is distributing each of the
1024 root cases by using a particular strategy regardless of the actual
task execution. For instance, a handler might off-load certain tasks to
a GPU, to a different execution context or even to a cluster.
Implementing a new way of distribution only requires writing a new Handler
and plug-it into the benchmark. Reading the code of existing handlers should
be sufficient to write a custom one.

B) Sub-Task execution refers to the actual execution of all sub-tasks
regardless of the global distribution strategy. More precisely, the global task
distribution only requires that a root-case gets compared to all other 1024 cases
but how to do that is up to the actual execution. A trivial execution would just compare
one by one. Another execution might use an Array of structs to exploit parallelism further.
For defining a new task execution approach, in total, three steps are required:

1. Implementing CaseReasonerI interface with a custom class
2. Creating a task in app
3. Test, test, test

## Implementing  CaseReasonerI interface

All computation methods are located in the SimilarityCalculation trait.
There are two traits to available, a normal one and an AOS trait. The latter
does the same job by using SIMD on an array. This is also known as Array of Structs
(AOS) thus the name. Performance wise, SIMD is at least twice as fast so just use it by default
and don't look back. Starting with your greatReasoner implementation, the starting
point would be something like:

    trait MyGreatReasoner extends CaseReasonerI with SimilarityCalculation_AOS {

The interface requires two methods to be implemented:

* getMostSimilarCase
* getMostSimilarCases(nrCases: Int,...)

Both methods are doing more or less the same so the best practice is defining
a private method "getSimilarCases" which returns the sortableValueMap containing all
similar cases. Implementing the two interface methods just requires returning either the
one or more entries from the map.

Implementing  getSimilarCases requires a few steps:
* creating an empty sortable map (SVM)
* calculate all similar cases
* adding all results to the SVM
* sorting the SVM

The last step is required to have constant time access to
the most similar case.

Your concurrent / parallelism strategy comes down to how
to calculate all similarity scores for a given case.
Taking the FutureArr implementation as example:

       val f: Future[Array[Double]] = calcSimilarityArrayFut(refCase, ca, weights).mapTo[Array[Double]]
       // it's blocking here
      val arr: Array[Double] = Await.result(f, T)

By passing an array (ca) to the calcSimilarityArrayFut method,
there is just one method call per case instead of 1024 versus the non-array version
which would create one call per comparison. However,  calcSimilarityArrayFut
only wraps the calculation in a promise in order to return a future.

  def calcSimilarityArrayFut(...): Future[Array[Double]] = {

    Promise.successful(calcSimilarity(refCase, c2, w)).future
  }

For a custom implementation, for instance map-reduce, these two methods need
to be replaced.


## Creating a task in app


Creating a task for execution is simple and requries only a few steps:

* defining a boolean flag
* defining a name, file names etc
* creating an instance of MyGreatReasoner
* calling benchmark & cleanup

Example below illustrates the steps:


     if (runALL || runFutureParArr) {
        //parallel case
        lazy val fut_arr_name = "Futures Par Arr"
        lazy val fut_arr_data_out: File = new File(p + "future_par_arr.xml")
        lazy val fut_arr_stats: File = new File(p + "futures_par_arr.xls")
        //
        lazy val cr = new CaseReasoner_Futures_ARR
        /* future par. coll run */
        runBenchmark(fut_arr_name, cr, handler, fut_arr_stats, fut_arr_data_out)
        //cleanup
        cleanUp()
      }



Happy hacking:-)