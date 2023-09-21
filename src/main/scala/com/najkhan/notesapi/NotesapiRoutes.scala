package com.najkhan.notesapi
//
import cats.effect.Async
import cats.effect.unsafe.implicits.global
import cats.implicits._
import com.najkhan.notesapi.request.{GetNoteByIdReq, GetNotesReq}
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import services.GetNotesService

object NotesapiRoutes {
  def getNotesRoutes[F[_] :Async](N :GetNotesService[F]) :HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "getnotes" / userid =>
         for {
           notesIO  <- N.getNotes(GetNotesReq(userid))
           resp <- Ok(notesIO.unsafeRunSync())
        } yield resp

      case GET -> IntVar(userId) /: IntVar(noteId) /: _ =>
        for {
          noteIo <- N.getNote(GetNoteByIdReq(userId.toString, noteId.toString))
          resp <- Ok(noteIo.unsafeRunSync())
        } yield resp
    }
  }
}