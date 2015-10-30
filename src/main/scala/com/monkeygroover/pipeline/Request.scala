package com.monkeygroover.pipeline

case class Request(payload: String) {

  def failWith(error: Throwable): Unit = ???
//    responder ! {
//      error match {
//        case RejectionError(rejection) ⇒ Rejected(rejection :: Nil)
//        case x                         ⇒ Status.Failure(x)
//      }
//    }

}
