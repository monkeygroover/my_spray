package com.monkeygroover.pipeline

import com.monkeygroover.pipeline.Directives._
import com.monkeygroover.pipeline.FutureDirectives._
import com.monkeygroover.pipeline.HListable._
import shapeless._

import scala.concurrent.Future

object Main extends App {

  import scala.concurrent.ExecutionContext.Implicits.global

  case class Started()

  val asyncOp = Future.successful("woo")

  val route = logEvent {
    onSuccess(asyncOp) { result: String =>
        complete {
          Response(result)
        }
    }
  }

  route(Request("add"))

}
