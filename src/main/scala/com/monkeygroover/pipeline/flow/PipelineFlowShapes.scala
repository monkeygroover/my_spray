package com.monkeygroover.pipeline.flow

import akka.stream.FanOutShape._
import akka.stream._
import akka.stream.scaladsl._

trait PipelineFlowShapes {

  class StepShape[N, T](_init: Init[Boolean] = Name[Boolean]("StepShape"))
    extends FanOutShape[Boolean](_init) {
    val next = newOutlet[N]("next")
    val terminate = newOutlet[T]("terminate")

    protected override def construct(i: Init[Boolean]) = new StepShape(i)
  }

  // This next bit is the Flow (FlexiRoute) that does the "smart fanout" based on the switch logic
  // using the FanOutShape designed above.
  case class StepRouter[N, T](n: N, t: T)
    extends FlexiRoute[Boolean, StepShape[N, T]](new StepShape, Attributes.name("StepShape")) {

    import FlexiRoute._

    override def createRouteLogic(p: PortT) = new RouteLogic[Boolean] {
      override def initialState = State[Any](DemandFromAll(p.next, p.terminate)) {
        (ctx, _, element) =>
          if (element) ctx.emit(p.next)(n)
          else ctx.emit(p.terminate)(t)

          SameState
      }

      override def initialCompletionHandling = eagerClose
    }
  }

}

object PipelineFlowShapes extends PipelineFlowShapes

