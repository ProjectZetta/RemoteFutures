# Distributed Remote Futures

Distributed programming but simple, efficient and very fast.

<!-- [![Build Status](https://secure.travis-ci.org/scala/async.png)](http://travis-ci.org/scala/async) -->
[![Build Status](https://travis-ci.org/DistributedRemoteFutures/DistributedRemoteFutures.svg?branch=master)](https://travis-ci.org/DistributedRemoteFutures/DistributedRemoteFutures)


## About

The project "DistributedRemoteFutures" provides a programming model that seamlessly integrates additional cloud computing resources
but without tampering with distributed models and code. It does so by extending Scala's Futures towards a remote execution for simply
adding more computational power according to changing demand. In the end, distributed remote futures allow you:
- simple and efficient distributed execution
- very fast to program
- easy to maintain.

## Usage and syntax

### A simple future that is executed remotely
```scala
val rf: Future[String] = Remote {
  Future {"asdf"}
}
```

### A simple future that is executed *remotely* and then mapped *locally*
```scala
val rf : Future[Int] = Remote {
  Future {"asdf"}
}.map( x => x.length )
```

### A simple future that is executed *remotely* and then mapped *remotely*
```scala
val rf : Future[Int] = Remote {
  Future {"asdf"}.map( x => x.length )
}
```

### Two simple futures, both executed *remotely*, but joined *locally*
```scala
val rf1 = Remote { Future { "asdf" }}
val rf2 = Remote { Future { "ghjk" }}

val lf : Future[String] = for {
  v1 <- rf1
  v2 <- rf2
} yield v1 + v2
```

### A collection that executes the map function remotely
```scala
// setup input list to compute Fibonacci numbers for
val xs: List[Long] = (1000000 to (1000000 + 1000)).toList

// type of fs is List[Future[BigInt]]
val fs = xs.map( Remote(x => Future{FibonacciComputations.fibBigInt(x)}) )

val r: Future[List[BigInt]] = Future sequence (fs)
```

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


