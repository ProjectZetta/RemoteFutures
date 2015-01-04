package org.remotefutures.proposals.remotable

import scala.reflect.macros.Context
import scala.reflect.runtime.universe._
import scala.language.experimental.macros

object Desugar {
  def _desugar(c: Context)(expr: c.Expr[Any]): c.Expr[Unit] = {
    import c.universe._
    println(show(expr.tree))
    reify {}
  }

  def desugar(expr: Any) = macro _desugar
}