package com.dminyaev.task.service

import com.dminyaev.task.dao.AccountsDao
import com.dminyaev.task.model.domain.Account
import com.dminyaev.task.model.errors.ApplicationError
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{Future, _}
import scalaz.EitherT

/**
  * Created by dminyaev.
  */
class AccountsService(accountDao: AccountsDao) extends LazyLogging {

  def getAccount(id: Int)(implicit ec: ExecutionContext): EitherT[Future, ApplicationError, Account] = accountDao.getAccount(id)

}
