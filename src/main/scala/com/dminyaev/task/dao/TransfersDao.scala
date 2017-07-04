package com.dminyaev.task.dao

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

import com.dminyaev.task.model.domain.Transfer
import com.dminyaev.task.model.errors.{ApplicationError, SameAccountTransfer, TransferNotFound}

import scala.collection.JavaConverters
import scala.concurrent.Future
import scalaz.{-\/, EitherT, \/-}
/**
  * Created by dminyaev.
  */
class TransfersDao {

  private val transfers = JavaConverters.mapAsScalaMapConverter(new ConcurrentHashMap[Int, Transfer]()).asScala

  private val keyGenerator = new AtomicInteger(0)

  def create(from: Int, to: Int, amount: Int): EitherT[Future, ApplicationError, Transfer]  = {
    EitherT[Future, ApplicationError, Transfer](Future.successful {
      if(from == to) {
        -\/(SameAccountTransfer(from))
      } else {
        val t = Transfer(keyGenerator.getAndIncrement(), from, to, amount)
        transfers.put(t.key, t)
        \/-(t)
      }
    })
  }

  def get(key: Int): EitherT[Future, ApplicationError, Transfer] = {
    EitherT(Future.successful {
      transfers.get(key) match {
        case Some(transfer) => \/-(transfer)
        case None => -\/(TransferNotFound(key))
      }
    })
  }
}

