package com.dminyaev.task.model.domain

/**
  * Created by dminyaev.
  */
class Transfer(val key: Int, val from: Int, val to: Int, val amount: Int, var status: TransactionStatus) {

  def updateStatus(s: TransactionStatus) = {
    status = s
  }
}

object Transfer {
  def apply(key: Int, from: Int, to: Int, amount: Int, status: TransactionStatus = Pending): Transfer
  = new Transfer(key, from, to, amount, status)
}

trait TransactionStatus
case object Executed extends TransactionStatus
case object Pending extends TransactionStatus