Distributed Remote Futures
==========================

<!-- [![Build Status](https://secure.travis-ci.org/scala/async.png)](http://travis-ci.org/scala/async) -->
[![Build Status](https://travis-ci.org/DistributedRemoteFutures/DistributedRemoteFutures.svg?branch=master)](https://travis-ci.org/DistributedRemoteFutures/DistributedRemoteFutures)

Futures are the abstraction of asynchronous execution of code, whose result will be eventually available. 

The main purpose of this project is to extend the execution context of futures to gain advantage from the computational power of remote nodes.
Instead of using the local maschine's capabiliies only, the execution of futures is extended towards a distributed system
while still fulfilling a concise, but not intrusive syntax and programming model.

Thus, extending this context of execution yields substantial gain in execution speed and performance
by the extension to nodes of a distributed system, without introducing more complexity from the developer's perspective.


This is work in progress with daily changes.



Requirements:
--------

1) JDK 7 [Download](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html)

2) SBT 0.13 [Download] (http://www.scala-sbt.org/release/docs/Getting-Started/Setup.html)

3) Scala 2.10 / 2.11 [Download] (http://scala-lang.org/download/)



Getting started
-----------------

    git clone https://github.com/DistributedRemoteFutures/DistributedRemoteFutures.git
    sbt compile


Contribute
----------
- Issue Tracker: https://github.com/DistributedRemoteFutures/DistributedRemoteFutures/issues?state=open

- Source Code: https://github.com/DistributedRemoteFutures/DistributedRemoteFutures

Licence
----------

This software is licensed under the Apache 2 license.


