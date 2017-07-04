package com.dminyaev.task.dao

import java.util.concurrent.ConcurrentHashMap

import com.dminyaev.task.model.domain.Account
import com.dminyaev.task.model.errors.{AccountNotFound, ApplicationError}

import scala.collection.JavaConverters
import scala.concurrent.Future
import scalaz.{-\/, EitherT, \/-}

/**
  * Created by dminyaev.
  */
class AccountsDao {

  private val accounts = JavaConverters.mapAsScalaMapConverter(new ConcurrentHashMap[Int, Account]()).asScala

  {
    accounts.put(1, new Account(1, 740))
    accounts.put(2, new Account(2, 350))
    accounts.put(3, new Account(3, 740))
    accounts.put(4, new Account(4, 350))
    accounts.put(5, new Account(5, Int.MaxValue))
  }

  def getAccount(id: Int): EitherT[Future, ApplicationError, Account] = {
    EitherT(Future.successful {
      accounts.get(id) match {
        case Some(account) => \/-(account)
        case None => -\/(AccountNotFound(id))
      }
    })
  }

}
