package com.monkeygroover.pipeline

import shapeless.{HNil, ::, HList}
import Directives._
import FutureDirectives._
import HListable._

import scala.concurrent.Future

object Main extends App {

  import scala.concurrent.ExecutionContext.Implicits.global

  val asyncOp = Future.successful("woo")

  val route = //logEvent {
    onSuccess(asyncOp) { result: String =>
      println(result)
      complete {
        Response(result)
      }
    }

  route(Request("add"))

}
