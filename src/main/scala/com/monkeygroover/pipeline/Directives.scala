package com.monkeygroover.pipeline

import shapeless.{HNil, ::, HList}
import shapeless.ops.function.FnToProduct
import shapeless.ops.hlist.Prepend

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

//  def logEvent[E](event: E): Directive0 =
//  {
//    println(event)
//  }

//  def host: Directive[String :: HNil] = {
//    extract { case Request(url) =>
//      val Array(protocol, rest) = url.split("://")
//      rest.split("/", 1).head :: HNil
//    }
//  }
//  def protocol: Directive[String :: HNil] = {
//    extract { case Request(url) =>
//      val Array(protocol, rest) = url.split("://")
//      protocol :: HNil
//    }
//  }

  def extract[L <: HList](f: Request => L): Directive[L] = new Directive[L] {
    def happly(inner: L => Route): Route = ctx => inner(f(ctx))(ctx)
  }

  type Directive0 = Directive[HNil]
  type Directive1[T] = Directive[T :: HNil]
}

object Directives extends Directives
