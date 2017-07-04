package com.dminyaev.task.model

/**
  * Created by dminyaev.
  */
package object responses {

  case class BalanceView(accountId: Int, total: Int)

  object BalanceView extends spray.json.DefaultJsonProtocol {
    implicit val format = jsonFormat2(BalanceView.apply)
  }

  case class TransferView(from: Int, to: Int, amount: Int, status: String)

  object TransferView extends spray.json.DefaultJsonProtocol {
    implicit val format = jsonFormat4(TransferView.apply)
  }

  case class ErrorMessage(code: Int, message: String)

  object ErrorMessage extends spray.json.DefaultJsonProtocol {
    implicit val format = jsonFormat2(ErrorMessage.apply)
  }

}
