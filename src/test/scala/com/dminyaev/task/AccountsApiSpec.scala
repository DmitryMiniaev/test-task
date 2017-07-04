package com.dminyaev.task

import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.dminyaev.task.api.AccountsApi
import com.dminyaev.task.dao.AccountsDao
import com.dminyaev.task.model.responses.BalanceView
import com.dminyaev.task.service.AccountsService
import com.softwaremill.macwire._
import org.scalatest._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import com.dminyaev.task.api.AccountsApi
import com.dminyaev.task.dao.AccountsDao
import com.dminyaev.task.service.AccountsService


class AccountsApiSpec extends FlatSpec with Matchers with ScalatestRouteTest {
  override def testConfigSource = "akka.loglevel = WARNING"

  val dao = wire[AccountsDao]
  val service = wire[AccountsService]
  val api = wire[AccountsApi]

  "Account api" should "get account info" in {
    Get("/api/v1/accounts/1/balance/") ~> api.routes ~> check {
      status shouldBe OK
      contentType shouldBe `application/json`
      responseAs[BalanceView] shouldBe BalanceView(1, 740)
    }
  }
}
