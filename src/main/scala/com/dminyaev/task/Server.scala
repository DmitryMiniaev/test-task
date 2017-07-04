package com.dminyaev.task

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.dminyaev.task.api.{AccountsApi, TransfersApi}
import com.dminyaev.task.dao.{AccountsDao, TransfersDao}
import com.dminyaev.task.service.{AccountsService, TransfersService}
import com.dminyaev.task.api.{AccountsApi, TransfersApi}
import com.dminyaev.task.dao.{AccountsDao, TransfersDao}
import com.dminyaev.task.service.{AccountsService, TransfersService}
import com.softwaremill.macwire._
import com.typesafe.config.ConfigFactory

object Server extends App {
  implicit val system = ActorSystem()
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val config = ConfigFactory.load()
  val logger = Logging(system, getClass)

  val accountsDao = wire[AccountsDao]
  val transfersDao = wire[TransfersDao]
  val accountsService = wire[AccountsService]
  val transfersService = wire[TransfersService]

  val accountsApi = wire[AccountsApi]
  val transfersApi = wire[TransfersApi]

  Http().bindAndHandle(accountsApi.routes ~ transfersApi.routes, "0.0.0.0", 9000)
}
