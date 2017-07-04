package com.dminyaev.task.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import com.dminyaev.task.model.errors.{TransferNotFound, TransferWasExecuted}
import com.dminyaev.task.model.requests.TransferCreateRequest
import com.dminyaev.task.model.responses.{ErrorMessage, TransferView}
import com.dminyaev.task.service.TransfersService
import com.typesafe.scalalogging.LazyLogging
import spray.json._

import scala.concurrent.ExecutionContext
import scalaz.std.scalaFuture._
import scalaz.{-\/, \/-}

/**
  * Created by dminyaev.
  */
class TransfersApi(transfersService: TransfersService)(implicit executionContext: ExecutionContext) extends LazyLogging {

  val routes = {
    pathPrefix("api") {
      pathPrefix("v1") {
        pathPrefix("transfers") {
          (pathEndOrSingleSlash & post & entity(as[TransferCreateRequest])) { transferRequest =>
            val from = transferRequest.from
            val to = transferRequest.to
            val amount = transferRequest.amount
            onComplete(
              transfersService
                .create(from, to, amount)
                .map(t => t.key)
                .run
            ) {
              case scala.util.Success(\/-(result)) => complete(Created -> JsNumber(result))
              case scala.util.Success(-\/(error)) => complete(BadRequest -> ErrorMessage(error.code(), error.msg()).toJson)
              case scala.util.Failure(e) =>
                logger.error("something went wrong", e)
                complete(InternalServerError -> None)
            }
          } ~
            (pathPrefix(IntNumber) & pathEndOrSingleSlash) { transferId =>
              put {
                onComplete(
                  transfersService
                    .executeTransfer(transferId)
                    .run
                ) {
                  case scala.util.Success(\/-(result)) => complete(OK -> None)
                  case scala.util.Success(-\/(error: TransferWasExecuted)) => complete(OK -> None)
                  case scala.util.Success(-\/(error)) => complete(BadRequest -> ErrorMessage(error.code(), error.msg()).toJson)
                  case scala.util.Failure(e) =>
                    logger.error("something went wrong", e)
                    complete(InternalServerError -> None)
                }
              } ~
              get {
                onComplete(
                  transfersService
                    .get(transferId)
                    .map(t => TransferView(t.from, t.to, t.amount, t.status.toString))
                    .run
                ) {
                  case scala.util.Success(\/-(result)) => complete(OK -> result.toJson)
                  case scala.util.Success(-\/(error: TransferNotFound)) => complete(NotFound -> ErrorMessage(error.code(), error.msg()).toJson)
                  case scala.util.Success(-\/(error)) => complete(BadRequest -> ErrorMessage(error.code(), error.msg()).toJson)
                  case scala.util.Failure(e) =>
                    logger.error("something went wrong", e)
                    complete(InternalServerError -> None)
                }
              }
            }
        }
      }
    }
  }
}
