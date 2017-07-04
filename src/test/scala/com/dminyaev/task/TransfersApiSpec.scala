package com.dminyaev.task

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.dminyaev.task.api.{AccountsApi, TransfersApi}
import com.dminyaev.task.dao.{AccountsDao, TransfersDao}
import com.dminyaev.task.model.errors.{InsufficientBalanceAmount, SameAccountTransfer, ToBigTransferAmount}
import com.dminyaev.task.model.requests.TransferCreateRequest
import com.dminyaev.task.model.responses.{BalanceView, ErrorMessage, TransferView}
import com.dminyaev.task.service.{AccountsService, TransfersService}
import com.softwaremill.macwire._
import org.scalatest._

class TransfersApiSpec extends FlatSpec with Matchers with ScalatestRouteTest {
  override def testConfigSource = "akka.loglevel = WARNING"

  val accountsDao = wire[AccountsDao]
  val transfersDao = wire[TransfersDao]
  val accountsService = wire[AccountsService]
  val transfersService = wire[TransfersService]

  val accountsApi = wire[AccountsApi]
  val transfersApi = wire[TransfersApi]

  "Transfer api" should "create transfer" in {
    Post("/api/v1/transfers", TransferCreateRequest(1, 2, 100)) ~> transfersApi.routes ~> check {
      status shouldBe Created
      contentType shouldBe `application/json`
      responseAs[String] shouldBe "0"
    }
  }

  it should "not create transfer on same account" in {
    Post("/api/v1/transfers", TransferCreateRequest(1, 1, 100)) ~> transfersApi.routes ~> check {
      status shouldBe BadRequest
      contentType shouldBe `application/json`
      responseAs[ErrorMessage] shouldBe ErrorMessage(5, SameAccountTransfer(1).msg())
    }
  }

  it should "get created transfer" in {
    Get("/api/v1/transfers/0") ~> transfersApi.routes ~> check {
      status shouldBe OK
      contentType shouldBe `application/json`
      responseAs[TransferView] shouldBe TransferView(1, 2, 100, "Pending")
    }
  }

  it should "handle get non existing transfer" in {
    Get("/api/v1/transfers/2") ~> transfersApi.routes ~> check {
      status shouldBe NotFound
      contentType shouldBe `application/json`
    }
  }

  it should "execute transfer" in {
    Post("/api/v1/transfers", TransferCreateRequest(1, 2, 100)) ~> transfersApi.routes ~> check {
      status shouldBe Created
      contentType shouldBe `application/json`
      responseAs[String] shouldBe "1"
    }

    Get("/api/v1/accounts/1/balance/") ~> accountsApi.routes ~> check {
      status shouldBe OK
      contentType shouldBe `application/json`
      responseAs[BalanceView] shouldBe BalanceView(1, 740)
    }
    Get("/api/v1/accounts/2/balance/") ~> accountsApi.routes ~> check {
      status shouldBe OK
      contentType shouldBe `application/json`
      responseAs[BalanceView] shouldBe BalanceView(2, 350)
    }

    Put("/api/v1/transfers/1/", None) ~> transfersApi.routes ~> check {
      status shouldBe OK
    }

    Get("/api/v1/accounts/1/balance/") ~> accountsApi.routes ~> check {
      status shouldBe OK
      contentType shouldBe `application/json`
      responseAs[BalanceView] shouldBe BalanceView(1, 640)
    }
    Get("/api/v1/accounts/2/balance/") ~> accountsApi.routes ~> check {
      status shouldBe OK
      contentType shouldBe `application/json`
      responseAs[BalanceView] shouldBe BalanceView(2, 450)
    }

  }

  it should "not execute transfer if account have insufficient amount" in {
    Post("/api/v1/transfers", TransferCreateRequest(1, 2, 100500)) ~> transfersApi.routes ~> check {
      status shouldBe Created
      contentType shouldBe `application/json`
      responseAs[String] shouldBe "2"
    }

    Put("/api/v1/transfers/2/", None) ~> transfersApi.routes ~> check {
      status shouldBe BadRequest
      contentType shouldBe `application/json`
      responseAs[ErrorMessage] shouldBe ErrorMessage(3, InsufficientBalanceAmount(1).msg())
    }
  }

  it should "not execute transfer that will overflow balance" in {
    Post("/api/v1/transfers", TransferCreateRequest(5, 1, Int.MaxValue)) ~> transfersApi.routes ~> check {
      status shouldBe Created
      contentType shouldBe `application/json`
      responseAs[String] shouldBe "3"
    }

    Get("/api/v1/accounts/5/balance/") ~> accountsApi.routes ~> check {
      status shouldBe OK
      contentType shouldBe `application/json`
      responseAs[BalanceView] shouldBe BalanceView(5, Int.MaxValue)
    }

    Put("/api/v1/transfers/3/", None) ~> transfersApi.routes ~> check {
      status shouldBe BadRequest
      contentType shouldBe `application/json`
      responseAs[ErrorMessage] shouldBe ErrorMessage(4, ToBigTransferAmount(1).msg())
    }
  }

  it should "not execute non existing transfer" in {
    Put("/api/v1/transfers/740/", None) ~> transfersApi.routes ~> check {
      status shouldBe BadRequest
    }
  }

}
