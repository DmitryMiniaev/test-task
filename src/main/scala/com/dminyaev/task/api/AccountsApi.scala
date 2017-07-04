package com.dminyaev.task.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import com.dminyaev.task.service.AccountsService
import com.dminyaev.task.model.errors.AccountNotFound
import com.dminyaev.task.model.responses.{BalanceView, ErrorMessage}
import com.typesafe.scalalogging.LazyLogging
import spray.json._

import scala.concurrent.ExecutionContext
import scalaz.std.scalaFuture._
import scalaz.{-\/, \/-}

/**
  * Created by dminyaev.
  */
class AccountsApi(accountService: AccountsService)(implicit executionContext: ExecutionContext) extends LazyLogging {

  val routes = {
    pathPrefix("api") {
      pathPrefix("v1") {
        (pathPrefix("accounts") & pathPrefix(IntNumber) & pathPrefix("balance") & pathEndOrSingleSlash) {
          id =>
            get {
              onComplete(
                accountService
                  .getAccount(id)
                  .map(acc => BalanceView(acc.id, acc.total))
                  .run
              ) {
                case scala.util.Success(\/-(result)) => complete(OK -> result.toJson)
                case scala.util.Success(-\/(error: AccountNotFound)) => complete(NotFound -> ErrorMessage(error.code(), error.msg()).toJson)
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
