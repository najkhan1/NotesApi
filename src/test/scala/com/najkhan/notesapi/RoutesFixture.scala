package com.najkhan.notesapi

import cats.conversions.all.autoWidenFunctor
import cats.effect._
import cats.effect.unsafe.IORuntime
import cats.syntax.all._
import com.najkhan.notesapi.errorHandling.UserHttpErrorHandler
import com.najkhan.notesapi.repositories.Repository
import com.najkhan.notesapi.request.{GetNoteByIdReq, GetNotesReq, SaveNoteReq}
import com.najkhan.notesapi.response.{RespGetNoteById, RespGetNotes}
import com.najkhan.notesapi.services.NotesService
import io.circe.Json
import io.circe.syntax.KeyOps
import org.http4s._

object RoutesFixture {

  implicit val runtime: IORuntime = cats.effect.unsafe.IORuntime.global
  val saveNotesReq = SaveNoteReq("1","title two", "note two")
  val saveNotesReqBadRequest = SaveNoteReq("2","title two", "note two")

  def checkGet[A](actual: IO[Option[Response[IO]]], expectedStatus: Status, expectedBody: Option[A])
                 (implicit ev: EntityDecoder[IO, A]): Boolean = {

    val actualResp = actual.unsafeRunSync().get
    println(actualResp.as[A].unsafeRunSync())
    println(expectedBody)
    val statusCheck = actualResp.status == expectedStatus
    val bodyCheck = expectedBody.fold[Boolean](
      actualResp.body.compile.toVector.unsafeRunSync().isEmpty)(
      expected => actualResp.as[A].unsafeRunSync() == expected
    )
    statusCheck && bodyCheck
  }

  def checkPost[A](actual: IO[Response[IO]], expectedStatus: Status, expectedBody: Option[A])
              (implicit ev: EntityDecoder[IO, A]): Boolean = {

    val actualResp = actual.unsafeRunSync()
    println(actualResp.as[A].unsafeRunSync())
    println(expectedBody)
    val statusCheck = actualResp.status == expectedStatus
    val bodyCheck = expectedBody.fold[Boolean](
      actualResp.body.compile.toVector.unsafeRunSync().isEmpty)(
      expected => actualResp.as[A].unsafeRunSync() == expected
    )
    statusCheck && bodyCheck
  }


  class TestRepo[F[_] : Sync]() extends Repository[F] {

    val getNotesRespByIdOne = RespGetNoteById("title one", "note 1")

    override def getNotesFromDatabase(notesReq: GetNotesReq): F[Option[RespGetNotes]] = {
      (notesReq match {
        case notesReq if notesReq.userId == "1" => (Some(RespGetNotes(notesReq.userId, List(getNotesRespByIdOne)))).pure[F]
        case _ => Some(RespGetNotes(notesReq.userId, List.empty[RespGetNoteById])).pure[F]

      })
    }

    override def getNoteByIdFromDatabase(noteReq: GetNoteByIdReq): F[Option[RespGetNoteById]] = {
      (noteReq match {
        case GetNoteByIdReq(_, _, _) if noteReq.userId == "1" && noteReq.noteId == "1" => Some(getNotesRespByIdOne)
        case _ => None
      }).pure[F]
    }

    override def saveNoteToDb(noteReq: SaveNoteReq): F[Int] = {

      (noteReq match {
        case SaveNoteReq(_, _, _) if noteReq.userId == "1" && !noteReq.title.isEmpty && !noteReq.note.isEmpty => 1
        case _ => 0
      }).pure[F]
    }
  }

  val service = new NotesService[IO](new TestRepo[IO])
  val notesGetRoutes = new NotesApiRoutes[IO](service)
  implicit val errorHandler: UserHttpErrorHandler[IO] = new UserHttpErrorHandler[IO]
  val notesSaveRoutes = new NotesPostRoutesApi[IO](service)
  val repIdOne = Json.obj(
    "Title" := "title one",
    "Note" := "note 1"
  )

  val repMultiNotes = Json.obj(
    "notes" := Json.arr(
      repIdOne
    )
  )

  val repNoNote = Json.obj(
    "Title" := "No Note exits for the ID",
    "Note" := ""
  )
  val repNoNotes = Json.obj("notes" := Json.arr())

  val saveResponseSuccess = Json.fromString("Note successfully saved")

  val saveResponseFailure = Json.fromString("Unable to save note to DB")

  val saveReqIdOne = Json.obj(
    "userId" := "1",
    "title" := "note title",
    "note" := "note itself"
  )
}
