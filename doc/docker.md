
# Getting started

1) Pull latest image

docker pull distributedremotefutures/dev-base:latest

2) Start image

docker run -i -t distributedremotefutures/dev-base:latest

and your have an open, interactive shell within the container.

3) The dev folder is:

home/dev/DistributedRemoteFutures/

The container has all java / scala / sbt / clone of git repo
& everything pre-compliled


# Docker & AWS integration 
RHEL Atomic Host Beta 
http://www.redhat.com/en/about/blog/small-footprint-big-impact-red-hat-enterprise-linux-7-atomic-host-beta-now-available

Kubernetes Docker Cluster managment.

Kubernetes enables users to ask a cluster to run a set of containers. 
The system automatically chooses hosts to run those containers on. 
Scaling, and availability is supported by auto-restarting, re-scheduling, and replicating containers. 

https://github.com/GoogleCloudPlatform/kubernetes/blob/master/DESIGN.md#the-kubernetes-control-plane 


## Links 


Reference: 
https://docs.docker.com/reference/builder/

Example:
https://github.com/kstaken/dockerfile-examples/blob/7d6f48d4b35c5dddf7b21ad95e9af0c93d17a3a8/couchdb/Dockerfile

Docker Explained: Using Dockerfiles to Automate Building of Images
https://www.digitalocean.com/community/tutorials/docker-explained-using-dockerfiles-to-automate-building-of-images

Dockerfile Best Practices
https://crosbymichael.com/dockerfile-best-practices.html

Dockerfile Best Practices - take 2
https://crosbymichael.com/dockerfile-best-practices-take-2.html

Advanced Docker Volumes
https://crosbymichael.com/advanced-docker-volumes.html
