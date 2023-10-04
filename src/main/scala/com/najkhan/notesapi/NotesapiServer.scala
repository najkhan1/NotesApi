package com.najkhan.notesapi

import cats.effect.IO
import com.comcast.ip4s._
import com.najkhan.notesapi.services.GetNotesService
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.middleware.Logger

object NotesapiServer {

  def run[F[_]]: IO[Nothing] = {

      val getNotesAlg = GetNotesService.impl[IO]

      // Combine Service Routes into an HttpApp.
      // Can also be done via a Router if you
      // want to extract segments not checked
      // in the underlying routes.
      val httpApp = (
        NotesapiRoutes.getNotesRoutes[IO](getNotesAlg)
      ).orNotFound

      // With Middlewares in place
      val finalHttpApp = Logger.httpApp(true, true)(httpApp)

      EmberServerBuilder.default[IO]
        .withHost(ipv4"0.0.0.0")
        .withPort(port"8080")
        .withHttpApp(finalHttpApp)
        .build

  }.useForever
}
