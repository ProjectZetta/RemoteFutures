/*
 * Copyright (c) 2014 Marvin Hansen.
*/
package org.remotefutures.controller.manager

/**
 * @author Marvin Hansen
 */
object CaseFileManager extends CaseFileManager

class CaseFileManager extends CaseFileManaging

trait CaseFileManaging extends FileManagerI with XmlFileMangerImpl