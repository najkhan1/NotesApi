package com.najkhan.notesapi

import cats.effect.Async
import com.comcast.ip4s._
import fs2.io.net.Network
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.middleware.Logger
import services.GetNotesService

object NotesapiServer {

  def run[F[_]: Async: Network]: F[Nothing] = {

      val getNotesAlg = GetNotesService.impl[F]

      // Combine Service Routes into an HttpApp.
      // Can also be done via a Router if you
      // want to extract segments not checked
      // in the underlying routes.
      val httpApp = (
        NotesapiRoutes.getNotesRoutes[F](getNotesAlg)
      ).orNotFound

      // With Middlewares in place
      val finalHttpApp = Logger.httpApp(true, true)(httpApp)


        EmberServerBuilder.default[F]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"8080")
          .withHttpApp(finalHttpApp)
          .build
  }.useForever
}
