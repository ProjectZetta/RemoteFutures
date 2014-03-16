Clarifying the exact goal
==========================

This is crucial for the actual contribution and gives answers to the questions

* What is the exact problem you want to solve
* Who experiences that problem
* How you want to solve that problem
* Why this is a better solution



@Martin
--------

Futures allow for the execution of asynchronous computation.
Unfortunately, futures in Java suffer from an API that enforces blocking of threads,
an efficient combination and concatenation of futures is hardly possible.
These issues have been identified and addressed by futures in Scala
(a modern programming languages, that combines the benefits of FP and OOP),
that can be composed and stacked while maintaining the property of asynchronous non-blocking
computation. Still, both Java and Scala futures lack one important feature:
Even though futures do run in parallel, through the use of threads spread across CPU cores,
they run only locally on one machine. What if programmers could not only run futures locally,
but also distribute the execution to a set of nodes, without having to give up
the ease of the Scala programming model? What if these programmers
could increase execution speed, just by adding additional nodes to their configuration file
and without any change of their code?

With the approach presented, the remote execution of futures
on remote machines will become feasible and is not any longer restricted
to only local machines. Futures are functions from zero input arguments to a return value,
which will eventually become available. Nevertheless, the future is executed
in a certain context, the so called closure. Mathematically speaking,
a future thus can be regarded as a function from the context to its return value.
In our approach, we capture the context of that function,
transfer the context to an remote node, execute the future and transfer
the result back to the calling site. This mechanism of abstraction
for the usage of different framework (Hazelcast, Akka, etc.)
to finally conduct the remote execution of futures.
The suggested approach allows scalability by additional remote machines
doing the execution of futures. Second, through the usage a concise syntax extensions,
which is minimally invasive and similar to the usage of regular Scala futures,
execution of futures can be leveraged to be executed remotely.


@Marvin
-------

Summary:

Distributed programming still requires a high level of expertise,
skills and specialized knowledge that hinders productivity. This problem
is faced by software engineers that build scalable systems accessed not
only used by ordinary pc systems but also by a fast growing number of mobile devices.

Remote.Futures solves this problem in three ways. First, remote functions
are written in the same way as local ones. Second, deployment, task distribution
and execution is done automatically by a RemoteExecutionContext. Third,
Fail-over and load balancing is already built-in by design so a developer
chose a suitable distribution strategy. Additionally, deployment is
minimized by switching from local to remote execution by just providing
a list of available nodes.





Answers:

* What is the exact problem you want to solve:

Distributed programming still requires a high level of expertise,
skills and specialized knowledge that makes it difficult to
accomplish in a reasonable time.

* Who experiences that problem

Each and every software engineer who needs to build systems that scales
with the number of users. The problem becomes more prevalent by
the ever growing number of mobile devices accessing internet services.

* How you want to solve that problem

Introducing Remote.Futures solves this problem in three ways:

1) Writing remote functions is done the same way as local functions

2) Remote deployment, task distribution and execution is managed automatically by a RemoteExecutionContext

3) Fail-over and load balancing is defined by specified by default strategies.
However, if no default strategies matches a particular use case, custom once can be added.

* Why this is a better solution

Remote.Futures have five unique advantages:

1) Simple yet powerful to code

2) Straight forward debugging

3) Extremely fast turn-around time.

4) Local testing build-in.

5) No-time deployment, just switch execution context and provide a list of Node IP addresses

Apart from that, Remote Futures "just works" out of the box.








