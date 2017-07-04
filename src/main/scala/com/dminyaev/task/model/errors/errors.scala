package com.dminyaev.task.model

/**
  * Created by dminyaev.
  */
package object errors {

  sealed trait ApplicationError {
    def code(): Int
    def msg(): String
  }

  final case class AccountNotFound(id: Int) extends ApplicationError {
    override def msg(): String = s"account $id not found"

    override def code(): Int = 0
  }

  final case class TransferNotFound(id: Int) extends ApplicationError {
    override def msg(): String = s"transfer $id not found"

    override def code(): Int = 2
  }

  final case class InsufficientBalanceAmount(accountId: Int) extends ApplicationError {
    override def msg(): String = s"can't transfer money: negative balance on account $accountId"

    override def code(): Int = 3
  }

  final case class ToBigTransferAmount(accountId: Int) extends ApplicationError {
    override def msg(): String = s"can't transfer money: to big transfer amount for account $accountId"

    override def code(): Int = 4
  }

  final case class SameAccountTransfer(accountId: Int) extends ApplicationError {
    override def msg(): String = s"can't transfer money to same account $accountId"

    override def code(): Int = 5
  }

  final case class TransferWasExecuted(id: Int) extends ApplicationError {
    override def msg(): String = s"transfer $id is already executed"

    override def code(): Int = 6
  }
}
