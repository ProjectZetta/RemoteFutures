/*
 * Copyright (c) 2014 Marvin Hansen.
*/
package org.remotefutures.utils

import java.util.{Date, Locale}
import java.text.DateFormat

/**
 * Util to output system and user locals on a system.
 *
 * @author Marvin Hansen
 */
object Locals {

  def ShowLanguageAndLocale() {
    javax.swing.JOptionPane.showMessageDialog(null, messages)
  }

  private final val messages: Array[String] = Array(
    // language and country System properties
    "user.language = " + System.getProperty("user.language"),
    "user.country = " + System.getProperty("user.country"),
    "user.language.display = " + System.getProperty("user.language.display"),
    "user.country.display = " + System.getProperty("user.country.display"),
    "user.language.format = " + System.getProperty("user.language.format"),
    "user.country.format = " + System.getProperty("user.country.format"),
    // default locales
    "default locale = " + Locale.getDefault,
    "default display locale = " + Locale.getDefault(Locale.Category.DISPLAY),
    "default format locale = " + Locale.getDefault(Locale.Category.FORMAT),
    // date test
    "Date: " + DateFormat.getDateInstance(DateFormat.FULL).format(new Date())
  )


}
