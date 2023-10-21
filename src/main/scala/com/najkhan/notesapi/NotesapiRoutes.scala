package com.najkhan.notesapi

import cats.effect.Sync
import cats.effect.kernel.Concurrent
import cats.implicits._
import com.najkhan.notesapi.errorHandling.{HttpErrorHandler, UserError}
import com.najkhan.notesapi.request.{GetNoteByIdReq, GetNotesReq, SaveNoteReq}
import com.najkhan.notesapi.response.{RespGetNoteById, RespGetNotes}
import com.najkhan.notesapi.services.NotesService
import org.http4s.{EntityDecoder, HttpRoutes}
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.circe.jsonOf
import org.http4s.dsl.Http4sDsl


trait NotesRoutes[F[_]] {
  def getNotesRoutes :HttpRoutes[F]
}

trait PostRoutes[F[_]] {
  def getPostNotesRoutes: HttpRoutes[F]
}

class NotesPostRoutesApi[F[_] :Concurrent](N: NotesService[F])(implicit H: HttpErrorHandler[F, UserError])  extends PostRoutes[F] {

  import io.circe.generic.auto._
  import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
  override def getPostNotesRoutes: HttpRoutes[F] = {

      val dsl = new Http4sDsl[F] {}
      import dsl._
      H.handle(HttpRoutes.of[F] {
        case rec @ POST -> Root / "savenote" =>

          implicit val userDecoder: EntityDecoder[F, SaveNoteReq] = jsonOf[F, SaveNoteReq]
          for {
            postReq <- rec.as[SaveNoteReq]
            rep <- N.saveNote(postReq)
            resp <- Ok(rep)
          } yield resp

      })

  }
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

      case _ =>
        NotFound("This path is not available")
    }
  }
  private def optionator[A](default :A, x :Option[A]) :A = {
    x.getOrElse(default)
  }
}
