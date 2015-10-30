package com.monkeygroover.pipeline.flow

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{FlowGraph, Merge, Sink, Source}
import PipelineFlowShapes.StepRouter
import shapeless.{:+:, CNil, Coproduct}

object Main extends App {


  case class Add(data: String)
  case class Accepted()
  case class Enabled()

  case class Succeeded()
  case class Rejected()
  case class Failed()

  type TerminalStates = Succeeded :+: Rejected :+: Failed :+: CNil

  implicit val as = ActorSystem()
  implicit val fm = ActorMaterializer()

  val s = Source(() => Seq(true, true, false, true).toIterator)

  val sink = Sink.foreach[TerminalStates](println _)

  val f = FlowGraph.closed(sink) { implicit builder => sink =>
    import FlowGraph.Implicits._

    val validator = builder.add(StepRouter(true, Coproduct[TerminalStates](Rejected())))

    val performer = builder.add(StepRouter(Coproduct[TerminalStates](Succeeded()), Coproduct[TerminalStates](Rejected())))

    val outputMerge = builder.add(Merge[TerminalStates](3))

    validator.terminate ~> outputMerge
    validator.next ~> performer.in

    performer.terminate ~> outputMerge
    performer.next ~> outputMerge

    val addFlow = (validator.in, outputMerge.out)

    s ~> validator.in
    outputMerge.out ~> sink
  }

  f.run()
}
