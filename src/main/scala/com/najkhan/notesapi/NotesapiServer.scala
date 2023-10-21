package com.najkhan.notesapi

import cats.effect.Async
import cats.implicits.toSemigroupKOps
import com.comcast.ip4s._
import com.najkhan.notesapi.databaseUtil.DbUtil
import com.najkhan.notesapi.errorHandling.UserHttpErrorHandler
import com.najkhan.notesapi.repositories.NotesRepository
import com.najkhan.notesapi.services.NotesService
import fs2.io.net.Network
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.middleware.Logger

object NotesapiServer {

  def run[F[_] : Async : Network]: F[Nothing] = {

      val dbConnection = new DbUtil[F]
      val notesRepo = new NotesRepository[F](dbConnection.transactor)
      val getNotesAlg = new NotesService[F](notesRepo)
      val getRoutes = new NotesApiRoutes[F](getNotesAlg)
      implicit val errorHandler = new UserHttpErrorHandler[F]()
      val postRouts = new NotesPostRoutesApi[F](getNotesAlg)

      val httpApp = (
        postRouts.getPostNotesRoutes <+> getRoutes.getNotesRoutes
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
