/* Copyright (c) 2014 Marvin Hansen.
 * www.marvin-hansen.tel.
 * ALl RIGHTS RESERVED
 ***************************
 * Project: DistributedRemoteFutures
 * User: Marvin Hansen
 * Web: www.marvin-hansen.tel
 * Date: 3/12/14 (/dd/mm/yy)
 * Time: 12:39 PM (CET)
 */
package org.remotefutures.core

import org.remotefutures.core.DistributionStrategy._

/**
 * Draft for more sophisticated task distribution.
 * Currently not used.
 */
//* companion object for static access i.e. imports */
object Distributor extends Distributor

// *composition trait, matches interface to implementation by name*/
trait Distributor extends DistributorComponent with DistributorComponentImpl

//** Interface */
trait DistributorComponent {

  protected val distributionService: DistributionService

  protected trait DistributionService {
    def distributeTask[T](Runnable: PromiseCompletingRunnable[T], dist: DistributionStrategy): Unit
  }

}

//*  Implementation */
trait DistributorComponentImpl extends DistributorComponent {

  override protected val distributionService: DistributionService = new DistributionServiceImpl

  private[this] class DistributionServiceImpl extends DistributionService {
    override def distributeTask[T](Runnable: PromiseCompletingRunnable[T], dist: DistributionStrategy): Unit = ???
  }

}



