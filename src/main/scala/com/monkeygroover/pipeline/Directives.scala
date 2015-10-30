package com.monkeygroover.pipeline

import shapeless._
import shapeless.ops.function.FnToProduct
import shapeless.ops.hlist.Prepend
import HListable._

trait Directives {

  case class Response(body: String)

  type Route = Request => Unit
  def complete[T](f: => T): Route = {
    ctx => println(f)
  }

  trait Directive[X <: HList] { self =>
    def happly(f: X => Route): Route
    def apply[F](f: F)(implicit fp: FnToProduct.Aux[F, X => Route]) = {
      happly(fp(f))
    }
    def &[Y <: HList](that: Directive[Y])(implicit prepend: Prepend[X, Y]): Directive[prepend.Out] = new Directive[prepend.Out] {
      override def happly(f: prepend.Out => Route): Route = {
        self.happly( x =>
          that.happly( y =>
            f(prepend(x, y))
          )
        )
      }
    }
  }

  def logEvent[L <: HList, E](event: E) = hprovide(HNil)


  //  def host: Directive[String :: HNil] = {
  //    extract { case Request(url) =>
  //      val Array(protocol, rest) = url.split("://")
  //      rest.split("/", 1).head :: HNil
  //    }
  //  }

  /**
   * Injects the given value into a directive.
   */
  def provide[T](value: T): Directive1[T] = hprovide(value :: HNil)

  /**
   * Injects the given values into a directive.
   */
  def hprovide[L <: HList](values: L): Directive[L] = new Directive[L] {
    def happly(f: L => Route): Route = ctx => f(values)
  }


  def extract[L <: HList](f: Request => L): Directive[L] = new Directive[L] {
    def happly(inner: L => Route): Route = ctx => inner(f(ctx))(ctx)
  }

  type Directive0 = Directive[HNil]
  type Directive1[T] = Directive[T :: HNil]
}

object Directives extends Directives
