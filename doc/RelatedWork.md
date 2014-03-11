
Related work
========

Traditional low level distributed programming based on C/C++ and FORTRAN
through Message Passing Interface1 (MPI) is complex and difficult to accomplish in
a reasonable time. The main source of complexity in programming distributed sys-
tems is dealing with system faults and interrupted or delayed network connections
(0). Because of the need to build highly scalable cloud systems in a short time, new
concepts, such as map reduce, actors and more recently distributed data flow
and novel concepts such as ad-hoc peer to peer computing, have emerged.

Distributed data flow extends data-flow (3) towards distributed computing
on streams (flow) of data. Map reduce is a distributed batch processing approach,
aiming at simpler programming of large scale offline data processing. Map reduce
is based on the concept of transferring the algorithm to the stored data, processing
them locally and returning only the computational result, which then will be
aggregated with other results processed in parallel (2). The strength of map reduce is
large scale data analytic; for instance, data mining on user data, but its weakness is
real-time data processing. Addressing the later issue, new frameworks such as
[Spark](https://spark.incubator.apache.org/) and [Storm](http://storm.incubator.apache.org/)
provide distributed real-time in-memory data processing.

Actors, on the other hand, are designed for real-time message processing (1). The
strength of actor systems is fault-tolerant high throughput processing, but its
weakness are computational-intense tasks. Apparently, the gain of high availability
architecture comes at the price of reduced performance and efficiency.

Distributed data flow is currently under active research but not used in practice,
mainly because of its very early stage. Even though the concept of distributed data
flow was first introduced in 2003 (4) and first applied to web service composition
(4), research on a solid theoretical foundation only started in 2009 (5) .
Despite important contributions, such as Live Distributed Objects (6) and scalable
protocols via distributed data flows (7), only BLOOM4 , a research programming
language based on Ruby implements distributed data flow (8). Bloom is a data-
centric programming language for disorderly distributed programming with consis-
tency analysis based on eventually consistency. The central concept of BLOOM is
based on the monotonic property, which is, a hypothesis of any derived fact may be
freely extended with additional assumptions. In programming, monotonicity means,
any extension in output needs to be derived of additional input. Consequently, any
retraction is considered to be non-monotonic.

However, as BLOOM is based on Ruby, it inherits Ruby’s dynamic type system and
byte code interpretation, which causes security and performance issues. In
particular, Ruby’s approach of ”Duck Typing”, that is guessing a type at run-time by only
checking the signature of a specific method, is problematic because it allows many ways to
break those run-time checks.

Ad-hoc peer to peer computing is prototyped by the Swarm project (9). The
portable continuation-based approach is based on type-directed selective CPS-transformation (10)
which moves arbitrary computation to a remote node for execution. However, there are two major
drawbacks, the first one is the inherent complexity related to selective CPS transformation
which makes code neither understandable nor maintainable. Also, there is a rather uncertain
future about the corresponding CPS plugin in Scala(11). Second, using the p2p network approach
has naturally no clear distribution strategy so that a computation hops several times
from one node to another until it gets eventually executed. Apart from the additional
delay and bandwidth expenses, optimizing this problem is complex and remains
unsolved by today.(12)

In summary, the related work is divided into three categories. The first one is large
scale distributed batch data processing using map reduce. The second one is real-
time message processing using actors, and the third on are novel methods
such as distributed data-flow or aA-hoc peer to peer computing.

The gap between all three categories is a general purpose distributed programming model
that is very fast and simple to program while efficiently utilising modern multi-core systems.

Bibliography
--------------

(0) J. Aspnes, C. Busch, S. Dolev, P. Fatourou, C. Georgiou, A. Shvartsman,
P. Spirakis, and R. Wattenhofer, “Eight open problems in distributed comput-
ing,” Bulletin of the European Association for Theoretical Computer Science,
vol. 90, pp. 109–126, Oct. 2006.
[PDF](http://www.cs.yale.edu/homes/aspnes/papers/beatcs-column-2006.pdf)


(1) C. Hewitt, “Actor Model of Computation,” Dana, pp. 1–29, 2010.
[PDF](http://arxiv.org/pdf/1008.1459.pdf)

(2) J. Dean and S. Ghemawat, “MapReduce: Simplified Data Processing on Large
Clusters,” OSDI, p. 13, 2004.
[Abstract](http://research.google.com/archive/mapreduce.html)
[PDF](http://research.google.com/archive/mapreduce-osdi04.pdf)


(3) A. Khan, D. Keskar, K. Vaid, and A. S. J. Vijayanand, “Data flow. Opening
the door to better ways of working in parallel,” 1994.
[Link](http://ieeexplore.ieee.org/xpl/articleDetails.jsp?reload=true&arnumber=283854)

(4) Dept David Liu and D. Liu, “Data-flow Distribution in FICAS Service
Composition Infrastructure,” in In Proceedings of the 15th International Conference
on Parallel and Distributed Computing Systems, 2003.
[PDF](http://eil.stanford.edu/ficas/papers/Dataflow.pdf)

(5) K. Ostrowski, K. Birman, D. Dolev, and C. Sakoda, “Implementing reliable
event streams in large systems via distributed data flows and recursive
delegation,” Proceedings of the Third ACM International Conference on Distributed
EventBased Systems DEBS 09, no. 1, p. 1, 2009.
[ACM](http://dl.acm.org/citation.cfm?id=1619279)
[PDF](http://www.cs.cornell.edu/~krzys/krzys_debs2009.pdf)


(6) K. Ostrowski, K. Birman, and D. Dolev, “Programming Live Distributed
Objects with Distributed Data Flows,” Language, 2009.
[Abstract](http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.218.9679)
[PDF](http://www.cs.cornell.edu/~krzys/krzys_oopsla2009.pdf)

(7) K. Ostrowski, “Recursion in Scalable Protocols via Distributed Data Flows,”
in Languages for Distributed Algorithms, 2012.
[Abstract](http://research.google.com/pubs/pub37478.html)
[PDF](http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.308.2583&rep=rep1&type=pdf)

(8) P. Alvaro, N. Conway, J. M. Hellerstein, and W. R. Marczak, “Consistency
Analysis in Bloom : a CALM and Collected Approach,” Systems Research,
vol. 3, no. 2, pp. 249–260, 2011.
[Abstract](http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.231.2991)
[PDF](http://db.cs.berkeley.edu/papers/cidr11-bloom.pdf)

(9) Swarm project on Github:
 https://github.com/sanity/Swarm

(10)
Tiark Rompf, Ingo Maier, and Martin Odersky. 2009.
Implementing first-class polymorphic delimited continuations by a type-directed selective CPS-transform.
In Proceedings of the 14th ACM SIGPLAN international conference on Functional programming (ICFP '09).
ACM, New York, NY, USA, 317-328.
DOI=10.1145/1596550.1596596
http://doi.acm.org/10.1145/1596550.1596596
[PDF](http://lampwww.epfl.ch/~rompf/continuations-icfp09.pdf)

[11]
https://groups.google.com/forum/#!msg/scala-internals/9Ts3GLsXuOg/36-Z6jhANMUJ
[12]
https://github.com/sanity/Swarm/issues/16
