
# Benchmark

This document contains further details about the benchmarking approach.


## Overview

In order to estimate the overall run-time performance of remote futures compared to
ordinary futures, a cased based reasoning benchmark on travel data is used.
In cased based reasoning (CBR), ”a new problem is solved by finding a similar past case,
and reusing it in the new problem situation” (1) I have chosen CBR as foundation for my
concurrency case study, because a case reasoning algorithm, correctly implemented,
exploits task and data parallelism.

A benchmark is used for executing a standard test case that compares each travel
case to the remaining 1023 cases and selects the most similar one. This task is performed
for all 1024 cases in the dataset. The benchmark application used for measuring performance is a
custom framework I’ve written following the best practice from Boyer et al. (4).
For statistical accuracy, a pre-run is executing the task 80 times before measuring 10
runs of the task. The long pre-run is motivated by excluding JVM optimization, such
as JIT compiling. Each of the 100 runs is measured independently to determine the
minimum, maximum and mean execution time. Furthermore, variance and standard
derivation are calculated in order to estimate the level of confidence in the results.
Execution time is measured in nanoseconds to circumvent an accuracy bug in Sys-
tem.currentTimeMillis(). Execution time is then converted to seconds by using a
precise custom conversion to prevent loss of accuracy. All measurements are ex-
ported to an excel sheet for further evaluation.


## Measurements Correctness

Software execution on the JVM is affected by a number of external factors, such as additional running
programs, x-windows system (Desktop on Windows), task scheduling of the operating system kernel
and a number of JVM optimizations. From JDK 5 onwards, the JVM applies a broad variety of
performance enhancements such as Just-in-time compilation, efficient inlining etc. In order produce
reliable and reproducible results that are statistically valid, a few considerations are required:

Correct benchmark preparation:
* Always run a benchmark as a stand-alone JAR outside an IDE
* Shutdown x-window systems
* Shutdown all non-required applications (top / htop will tell what's running)

These steps already improve noticeably the reproducibility of performance measurements.

Correct benchmark execution:
* Chose at least 60 pre-runs
* Chose an appropriate number of runs, i.e. 100
* Disable println (verbose = off)

The first step is approaching JVM optimization by initiating branch optimization and hot-spot compilation
without measuring it. However, if the first 60 runs are measured, the execution time actually decreases until
with each iteration but eventually stabilizes after around 50 runs. However, this is only true for the Client JVM.
A JVM configured as Server requires a much higher number, approximately 2000 runs, before all JVM optimazation is
completed.

An appropriate number number of runs is everything above 50. because it allows the correct calculation of statistical values
such as mean, median or variance. Disabling println avoids a certain overhead by printing to system console.



## Data and methodology

At the heart of any CBR system there is a similarity function that quantifies the
similarity of cases (2). In CBR, there are three categories of similarity models: ab-
solute, relative and metric similarity (3). I’ve chose a metric model that calculates
a global similarity score, based on the k nearest neighbour (K-NN) algorithm.
The applied formula is:


![Formular](http://example.com/images/logo.png?raw=true)

In the formula, T is the target case, S is the source case, n is the number of attributes
in each case and index i refers to an individual attribute from 1 to n. Function f
is a local similarity function for attribute i in cases T and S. Each local similarity
score is multiplied by its corresponding weight W, reflecting its relevance. The global
similarity score is defined as the sum of all weighted local similarities, divided by the
sum of all local weights. The division by the sum of all local weights is used for
normalizing the global score.
The data-set is a collection of 1024 cases of arbitrary travel bookings. Each data
record has twelve attributes and each attribute has corresponding local similarity
function used calculating the distance between two values of the same attribute.


## Architecture

The CBR benchmark consists of three core components:

1. Handler
2. Reasoner
3. Calculator

Handler

@TODO


Currently, there are four major implementations of the reasoner interface:

1. lin = linear execution
2. linArr = linear execution with data parallelism
3. ParArr = Parallel Collections with data parallelism
4. FuturesArr = Futures with data parallelism


The SimilarityCalculator trait (located in controller.calculator)
performs the actual CBR computation, that is calculating a similarity score for a case compared
to another case. A  reasoner is an abstraction layer above the calculator that provides two main methods:

* getMostSimilarCase
* getMostSimilarCases(nrCases)




## CBR Implementation

Conceptually cased based reasoning (CBR) is described by the CBR-cycle which
comprises four activities:

1. Retrieve similar cases to the problem description
2. Reuse a solution suggested by a similar case
3. Revise or adapt that solution to better fit the new problem
4. Retain the new solution once it has been confirmed or validated.

4. Retain the new solution once it has been confirmed or validated.
Case retrieval is further divided into querying existing cases and calculating a similarity score that
defines how similar two cases are to each other. For the scope of this benchmark, I
have implemented only the retrieve stage by loading all cases from a data file and
calculating a similarity score based on all attributes. A travel case is implemented
as a case class in Scala, as shown below:

        final case class Case(journeyCode: Int, caseId: String,
                              holidayType: HolidayType, price: Int,
                              numberOfPersons: Short, region: String,
                              country: Country, transportation: Transportation,
                              duration: Short, season: Month,
                              accommodation: Accommodation, hotel: Hotel)


A local similarity score for each attribute calculates the distance of the actual value of
from target to the source case. Taking the attribute numberOfPersons as an example,
if the number of persons is identical in both cases, the similarity score is one.
If the numbers differs, depending on how far the values are apart, a lower similarity
score is calculated.

The Listing  shows the implementation of the person similarity score. First, the similarity score is
only calculated if the attribute is set (1b); if not, its value is set to zero
(1b). Second, each calculated score is multiplied by the attribute weight to reflect
the attributes relevancy correctly. Third, if the values of person attribute are too far
apart, the score is set to zero in order to prevent an unnecessary bias of the global
similarity score.

          if (isSet) {
              if (c2Persons == refPersons) _ONE * w
              else if (c2Persons == refPersons + 1 || c2Persons == refPersons - 1) 0.90 * w
              else if (c2Persons == refPersons + 2 || c2Persons == refPersons - 2) 0.70 * w
              else if (c2Persons == refPersons + 3 || c2Persons == refPersons - 3) 0.50 * w
              else if (c2Persons == refPersons + 4 || c2Persons == refPersons - 4) 0.30 * w
              else if (c2Persons == refPersons + 5 || c2Persons == refPersons - 5) 0.05 * w
              else _NULL
            }
            else _NULL
          }


The remaining eleven attributes follow the same approach of determining the similarity
score on how far values are apart from each other. String attributes, such
as a region name, have a simplified similarity function that only determines if two
strings are identical or not. This is due to a shortcoming of the used data set, which
has no ontology of regions.


The global similarity score, as shown below, sums all attribute similarity scores
and divides the sum by the sum of all attribute weights.
The quality of a similarity function depends on how well the local similarity scores
are defined. This enforces a trade-off between accuracy in the sense of an accurate
distance metric that reflects the real difference between two cases and computational
cost.

          def calcSimilarity(refCase: Case, c2: Case, w: FeatureWeights): Double =

            (getDurationSimilarity(refCase.duration, ...)
              + getPersonsSimilarity(refCase.numberOfPersons, ...)
              + getPriceSimilarity(refCase.price, ...)
              + getRegionSimilarity(refCase.region, ...)
              + getTransportationSimilarity(refCase.transportation.id, ...)
              + getSeasonSimilarity(refCase.season.id, ...)
              + getAccommodationSimilarity(refCase.accommodation.id, ...)
              + getHotelSimilarity(refCase.hotel.toString, ...)
              + getHolidayTypeSimilarity(refCase.holidayType.id, ...)
              + getCountrySimilarity(refCase.country.countryCode, ...)
              )
              / w.SUM
        }

The more accurate a similarity function gets, the more expensive the comparison
between cases becomes, because calculating a more detailed scores requires more
comparisons and attribute checks.

## Optimization

In order to cope with the general high computational cost of case based reasoning,
the following generic performance optimization have been applied:

* Scala HashMap is replaced by java.util.HashMap.
* Lazy val is used for invariant computational results.
* private(this) is used to inline fields and small methods.
* Closures are avoided.
* Lists are replaced with arrays.
* Lookup tables are used whenever possible
* SIMD data parallelism is used whenever possible

Apart from these rather generic performance tweaks,
no optimization specifically to CBR has been used.

For clarifying what CBR specific performance optimization means, a few more de-
tails of CBR system are required. First, domain specific knowledge is often used to
apply task specific optimization to a CBR system. For instance, the first optimization would
be the reduction of query results by excluding cases that are obviously
dissimilar in order to reduce the number of comparisons. A second CBR specific
optimization would be caching results from comparisons that occur frequently, in
order to prevent recomputing the same scores again.
A third optimization would
be a reduction of attributes used for calculating the similarity score. By applying
entropy determining algorithms, such as InformationGain, only those few attributes
with the highest discrimination score could be used for calculating the similarity
score required for the CBR algorithm. This approach of dimension reduction has a
profound impact on the computational cost of the CBR algorithm, since a quarter
of all attributes are often enough to separate over 95% of all cases.


However, for this benchmark, none of these domain specific optimizations have
been applied. Instead, the design of the benchmark reflects the worst possible scenario
in a CBR system which means, all cases are compared to all others using all
attributes for similarity calculating.

This design was chosen by purpose to maximize the computational cost while keeping the actual run-time low
in order to measure the real impact of the implementation.
In contrast, a fully optimized CBR system using cached computational results would
arbitrary bias performance measurements.



## Validity

Validity of the benchmark results is approached by using excel for cross-validation of
all values calculated by the benchmark. Cross-validation means, min, max, average,
variance and standard derivation are calculated by corresponding excel functions on
the same raw data and compared to the results calculated by the benchmark. How-
ever, due to a rounding issue in Excel, the cross validation for variance and standard
derivation differs in Excel. I need to clarify that a simple check with a scientific
calculator proves excel wrong and verifies the results calculated by the benchmark.
This was not the intention of the cross-validation, but having at least min, max and
mean values verified increases the confidence in the presented results. Also, all raw-
data and spreadsheets are on the thesis website and can be further validated and
analyzed with better statistical tools such as R or SPSS.



## Bibliography

(1) A. Aamodt and E. Plaza, “Case-based reasoning: Foundational issues,
    methodological variations, and system approaches,”
    AI communications, vol. 7, no. 1, pp. 39–59, 1994.

(2) D. W. Aha, “The omnipresence of case-based reasoning in science and
    application,” Knowledge-based systems, vol. 11, no. 5, pp. 261–273, 1998.

(3) H. Osborne and D. Bridge, “Models of similarity for case-based reasoning,”
    in Procs. of the Interdisciplinary Workshop on Similarity and Categorisation,
    pp. 173–179, Citeseer, 1997.

(4)  Brent Boyer, “Robust Java benchmarking”, IBM developerWorks, 2008.
     http://www.ibm.com/developerworks/java/library/j-benchmark1/index.html











