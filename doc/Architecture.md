# Architecture

## Overview

The Architecture is using cake pattern for component development
and dependency management.

There core components are:

## RemoteExecutor

A remote executor executes a PromiseCompletingRunnable remotely
on a pool of nodes, a single node or a sub-group of nodes.

Possible implementations could be:

* Akka,
* Hazelcast
* BYO = Bring your own



