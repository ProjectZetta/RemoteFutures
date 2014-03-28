/*
 * Copyright (c) 2014 Marvin Hansen.
*/
package org.remotefutures.utils

import java.util.concurrent.TimeUnit

/**
 * @author Marvin Hansen
 */
case class StatsData(name: String, timeUnit: TimeUnit, data_arr: Array[Double],
                     min: Double, max: Double, mean: Double, variance: Double, stdDev: Double)
