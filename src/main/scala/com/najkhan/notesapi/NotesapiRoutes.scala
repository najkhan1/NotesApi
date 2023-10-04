package com.najkhan.notesapi
//
import cats.effect.IO
import com.najkhan.notesapi.request.{GetNoteByIdReq, GetNotesReq, SaveNotReq}
import com.najkhan.notesapi.services.GetNotesService
import io.circe.generic.auto._
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec.{circeEntityDecoder, circeEntityEncoder}
import org.http4s.dsl.Http4sDsl
//
//type Http[F[_]] = Kleisli[F, Request, Response]
//
//type HttpRoutes[F[_]] = Http[OptionT[F, ?]]
object NotesapiRoutes {


  def getNotesRoutes[F[_]](N :GetNotesService[F]) :HttpRoutes[IO] = {
    val dsl = new Http4sDsl[IO] {}
    import dsl._
    HttpRoutes.of[IO] {
      case GET -> Root / "getnotes" / userid =>
         for {
           notesIo <- N.getNotes(GetNotesReq(userid))
           resp <- Ok(notesIo)
        } yield resp

      case GET -> IntVar(userId) /: IntVar(noteId) /: _ =>
        for {
          noteIo <- N.getNote(GetNoteByIdReq(userId.toString, noteId.toString))
          //noteIo <- noteIoWildCard.asInstanceOf[IO[RespGetNoteById]]
          resp <- Ok(noteIo)
        } yield resp

      case req@ POST -> Root / "savenotes" =>
        for {
          note <- req.as[SaveNotReq]
          saveNote  :Int<- N.saveNote(note)
          resp <- Ok(saveNote)
        } yield resp

      case _ =>
        NotFound("This path is not available")
    }
  }

}