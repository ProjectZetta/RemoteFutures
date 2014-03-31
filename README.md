# Distributed Remote Futures

<!-- [![Build Status](https://secure.travis-ci.org/scala/async.png)](http://travis-ci.org/scala/async) -->
[![Build Status](https://travis-ci.org/DistributedRemoteFutures/DistributedRemoteFutures.svg?branch=master)](https://travis-ci.org/DistributedRemoteFutures/DistributedRemoteFutures)


## About

Not only the number of cores per CPU has tremendously increased but also the number of available PCs. [cite]
Therefore, it is desirable to provide a programming model that allows to profit by this extra computational power
in both execution speed and performance.
The project "DistributedRemoteFutures" provides a programming model, such that a programmer can easily make use of these additional resources
but without tampering with distributed models and code. Instead, the paradigm and model of Scala futures is chosen. This model is then extended
by a configuration based approach, such that the setup of the distributed environment ( e.g. remote nodes setup, strategy for load-balancing)
is easily possible.

In the end, distributed remote futures allow you
- to make distributed programming simple, efficient and
- very fast to program and
- easy to maintain.


## Example

To illustrate the key idea underlying a RemoteFuture, the example below
shows the basic usage in three steps. First, some imports:

    import org.remotefutures.core._

Second, a Remote Future is written in the same way one would write a Future in Scala,
for instance:

        object RemoteExampleSimple extends App {

            final val T = Duration(2, TimeUnit.SECONDS)

            val rmt = RemoteFuture {
            println(Thread.currentThread.getName)
            42 * 42 * 233
            }
         }

 Finally, a Remote seamlessly combines with a Future using monadic composition, for instance:

           val fut = Future {
             println(Thread.currentThread.getName)
             24 * 99 * 399
           }

           println("Combining remotes and futures ")
           val comb = for {
             r <- rmt
             f <- fut
           } yield r + f

           println("final result of remote AND future")
           comb onComplete {
             case Success(all) => println(all)
             case Failure(t) => println("An error happened: " + t.getMessage)
           }

 Output of the example looks like:

        Done, remote result is: 411012
        Done, future result is: 948024
        Combining remotes and futures
        Final result of remote AND future is: 1359036


More examples are in the [repo](https://github.com/DistributedRemoteFutures/DistributedRemoteFutures/tree/master/src/main/scala/org/remotefutures/examples)



## Background

Futures are the abstraction of asynchronous execution of code, whose result will be eventually available.

The main purpose of this project is to extend the execution context of futures to gain advantage from the computational power of remote nodes.
Instead of using the local machine's capabilities only, the execution of futures is extended towards a distributed system
while still fulfilling a concise, but not intrusive syntax and programming model.

Thus, extending this context of execution yields substantial gain in execution speed and performance
by the extension to nodes of a distributed system, without introducing more complexity from the developer's perspective.


Requirements:
--------

1) JDK 7 [Download](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html)

2) SBT 0.13 [Download] (http://www.scala-sbt.org/release/docs/Getting-Started/Setup.html)

3) Scala 2.10 / 2.11 [Download] (http://scala-lang.org/download/)



## Getting started

    git clone https://github.com/DistributedRemoteFutures/DistributedRemoteFutures.git
    sbt compile


## Contribute
- Issue Tracker: https://github.com/DistributedRemoteFutures/DistributedRemoteFutures/issues?state=open

- Source Code: https://github.com/DistributedRemoteFutures/DistributedRemoteFutures


## Team:
* [Martin Senne](https://github.com/MartinSenne/)
* [Marvin Hansen](https://github.com/marvin-hansen)


## Licnse

This software is licensed under the Apache 2 license.


