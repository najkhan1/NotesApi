package services

import cats.Applicative
import cats.effect.IO
import cats.implicits.catsSyntaxApplicativeId
import com.najkhan.notesapi.Dto.{GetNoteByIdDto, GetNotesDto}
import com.najkhan.notesapi.databaseUtil.DbUtil
import com.najkhan.notesapi.repositories.NotesRepository.{getNoteById, getNotesFromDb}
import com.najkhan.notesapi.request.{GetNoteByIdReq, GetNotesReq}
import com.najkhan.notesapi.response.{RespGetNote, RespGetNotes}
import doobie.WeakAsync.doobieWeakAsyncForAsync
import doobie.implicits._

trait GetNotesService[F[_]] {
  def getNotes(getNotesReq: GetNotesReq) :F[IO[RespGetNotes]]
  def getNote(getNoteReq: GetNoteByIdReq) :F[IO[RespGetNote]]

}

object GetNotesService {

  val transactor = DbUtil.transactor

  def impl[F[_] : Applicative ]: GetNotesService[F] = new GetNotesService[F] {
    final def getNotes(getNotesReq: GetNotesReq) :F[IO[RespGetNotes]] =
      getNotesFromDatabase(getNotesReq).pure[F]
    final def getNote(getNoteReq: GetNoteByIdReq) :F[IO[RespGetNote]] =
      getNoteByIdFromDatabase(getNoteReq).pure[F]
  }


  def getNotesFromDatabase( notesReq :GetNotesReq) = {
      getNotesFromDb(GetNotesDto(notesReq.userId)).
        to[List].
        transact(transactor)
        .map(notes => RespGetNotes(notesReq.requestId, notes))
  }

  def getNoteByIdFromDatabase(noteReq: GetNoteByIdReq) = {
    getNoteById(GetNoteByIdDto(noteReq.userId, noteReq.noteId))
      .to[List]
      .transact(transactor)
      .map{note =>
        val aNote = note.head
        RespGetNote(aNote.title, aNote.body)
  }

  }

}