package com.dminyaev.task.model

import spray.json.DefaultJsonProtocol

/**
  * Created by dminyaev.
  */
package object requests {

  case class TransferCreateRequest(from: Int, to: Int, amount: Int)

  object TransferCreateRequest extends DefaultJsonProtocol {
    implicit val format = jsonFormat3(TransferCreateRequest.apply)
  }
}
