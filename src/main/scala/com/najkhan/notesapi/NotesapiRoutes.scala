package com.najkhan.notesapi

import cats.effect.Sync
import cats.implicits._
import com.najkhan.notesapi.request.{GetNoteByIdReq, GetNotesReq}
import com.najkhan.notesapi.response.{RespGetNoteById, RespGetNotes}
import com.najkhan.notesapi.services.NotesService
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

trait NotesRoutes[F[_]] {
  def getNotesRoutes :HttpRoutes[F]
}
class NotesApiRoutes[F[_] :Sync](N :NotesService[F]) extends NotesRoutes[F] {

  final def getNotesRoutes :HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "getnotes" / userid =>
        val req = GetNotesReq(userid)
        val default = RespGetNotes(req.requestId,List.empty[RespGetNoteById])
        for {
           notesOp <- N.getNotes(GetNotesReq(userid))
           resp <- Ok(optionator(default, notesOp))
        } yield resp

      case GET -> IntVar(userId) /: IntVar(noteId) /: _ =>
        val req: GetNoteByIdReq = GetNoteByIdReq(userId.toString, noteId.toString)
        val default = RespGetNoteById("No Note exits for the ID","")
        for {
          noteIo <- N.getNote(req)
          resp <- Ok(optionator(default, noteIo))
        } yield resp
    }
  }

  def optionator[A](default :A, x :Option[A]) :A = {
    x.getOrElse(default)
  }
}

