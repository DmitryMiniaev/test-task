package com.dminyaev.task.model.domain

/**
  * Created by dminyaev.
  */
class Account(val id: Int, var total: Int) {

  def withdraw(amount: Int) = {
    total = total - amount
  }

  def deposit(amount: Int) = {
    total = total + amount
  }
}
object Account {
  def apply(id: Int, total: Int): Account = new Account(id, total)
}