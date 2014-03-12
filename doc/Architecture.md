Architecture:
=============

Overview
----------

The Architecture is using cake pattern for component development
and dependency management.

There are three core components:

1) RemoteExecutor
2) Distributor
3) DistributedRemoteExecutor


1) RemoteExecutor
------------------

A remote executor executes a PromiseCompletingRunnable remotely
on a pool of nodes, a single node or a sub-group of nodes.

Possible implementations could be:

* Akka,
* Hazelcast
* BYO = Bring your own

2) Distributor
--------------

A Distributor distributes tasks on a pool.

Even though this component is not used at the moment,
it should be the implementation place for more
advanced distribution strategies such as:

* LowLatencyHighAvailable
* MaxThroughput
* etc.


3) DistributedRemoteExecutor
-----------------------------

A DistributedRemoteExecutor
is a facade that dispatches calls
according to its distribution strategy
to the RemoteExecutor.

Currently, it does so by using a simple
pattern match over the DistributionStrategy
Enum.

The main purpose is the clear seperation of concerns
while fully preserving maintainability.

Maintainability
================


Maintainability is mainly accomplished by
a few special properties of the cake pattern:



1) Abstract Type overriding.
----------------------------

Swapping a DefaultRemoteExecutor implementation
for an akkaRemoteExecutor still returns an
RemoteExecutor thus no changes on the caller side.

2) Private implementations
----------------------------

Since all component implementations are located
in nested private classes, writting a new one simply
means adding another private nested class that is
fully isolated from all others.

3) Self Type directed dependency injection.
-------------------------------------------

Modules that are mixed out of simple components
(those without dependencies) only rely on the
abstract type of the component interface.
However, within a modul, the self-type gets
dispatched to the interface thus no dependency
on any implementation.

4) Constructing companion objects
---------------------------------

Swapping or testing implementations can and
should be done through constructing companion objects
linked to aliases.

The Alias is thus used for import but the actual link
to the companion object gets swapped in the Alias configuration.

The client code remains untouched but with a single line,
the entire backend gets swapped.

