package com.dminyaev.task.service

import com.dminyaev.task.dao.{AccountsDao, TransfersDao}
import com.dminyaev.task.model.domain.{Account, Executed, Pending, Transfer}
import com.dminyaev.task.model.errors._
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent._
import scalaz.std.scalaFuture._
import scalaz.{-\/, EitherT, \/, \/-}

/**
  * Created by dminyaev.
  */
class TransfersService(accountsDao: AccountsDao, transfersDao: TransfersDao) extends LazyLogging {

  private def validateSameAccount(from: Account, to: Account): ApplicationError \/ Unit = {
    if(from.id == to.id) {
      -\/(SameAccountTransfer(from.id))
    } else {
      logger.warn(s"same account validation failed for accounts (${from.id}, ${to.id})")
      \/-(())
    }
  }

  private def validateNegativeBalance(accountId: Int, currentBalance: Int, fransferAmount: Int): ApplicationError \/ Unit = {
    if(currentBalance - fransferAmount < 0) {
      -\/(InsufficientBalanceAmount(accountId))
    } else {
      logger.warn(s"negative balance validation failed for account $accountId, current balance: $currentBalance, transfer amount $fransferAmount")
      \/-(())
    }
  }

  private def validateTransferAmountQty(accountId: Int, currentBalance: Int, transferAmount: Int): ApplicationError \/ Unit = {
    if(currentBalance.toLong + transferAmount.toLong >= Int.MaxValue) {
      -\/(ToBigTransferAmount(accountId))
    } else {
      logger.warn(s"transfer amount qty validation failed for account $accountId, current balance: $currentBalance, transfer amount $transferAmount")
      \/-(())
    }
  }
  private def validateTransferStatus(t: Transfer): ApplicationError \/ Unit =
    if(t.status != Pending) -\/(TransferWasExecuted(t.key)) else \/-(())

  private def doTransfer(transfer: Transfer, from: Account, to: Account, amount: Int)(implicit ec: ExecutionContext): EitherT[Future, ApplicationError, Unit] = {
    EitherT(
      Future {
        blocking {
          transfer.synchronized {
            val (lock1, lock2) = if(from.id < to.id) (from, to) else (to, from)
            lock1.synchronized {
              lock2.synchronized {
                for {
                  _ <- validateTransferStatus(transfer)
                  _ <- validateNegativeBalance(from.id, from.total, amount)
                  _ <- validateTransferAmountQty(to.id, to.total, amount)
                } yield {
                  transfer.updateStatus(Executed)
                  from.withdraw(amount)
                  to.deposit(amount)
                }
              }
            }
          }
        }
      })
  }

  def create(from: Int, to: Int, amount: Int)(implicit ec: ExecutionContext): EitherT[Future, ApplicationError, Transfer] = {
    transfersDao.create(from, to, amount)
  }

  def get(id: Int)(implicit ec: ExecutionContext): EitherT[Future, ApplicationError, Transfer] = {
    transfersDao.get(id)
  }

  def executeTransfer(id: Int)(implicit ec: ExecutionContext): EitherT[Future, ApplicationError, Unit] = {
    for {
      t <- transfersDao.get(id)
      account1 <- accountsDao.getAccount(t.from)
      account2 <- accountsDao.getAccount(t.to)
      _ <- doTransfer(t, account1, account2, t.amount)
    } yield ()
  }

}
