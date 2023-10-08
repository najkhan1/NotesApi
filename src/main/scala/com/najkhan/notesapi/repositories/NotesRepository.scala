package com.najkhan.notesapi.repositories

import cats.effect.Sync
import cats.implicits.toFunctorOps
import com.najkhan.notesapi.Dto.{GetNoteByIdDto, GetNotesDto, SaveRequestDto}
import com.najkhan.notesapi.repositories.NotesRepository._
import com.najkhan.notesapi.request.{GetNoteByIdReq, GetNotesReq, SaveNoteReq}
import com.najkhan.notesapi.response.{RespGetNoteById, RespGetNotes}
import doobie.implicits._
import doobie.util.transactor.Transactor.Aux


trait Repository[F[_]] {

  def getNotesFromDatabase(notesReq: GetNotesReq): F[Option[RespGetNotes]]
  def getNoteByIdFromDatabase(noteReq: GetNoteByIdReq): F[Option[RespGetNoteById]]

  def saveNoteToDb(noteReq :SaveNoteReq) :F[Int]

}

class NotesRepository[F[_] :Sync](transactor: Aux[F, Unit]) extends Repository[F] {

  override def getNotesFromDatabase(notesReq: GetNotesReq): F[Option[RespGetNotes]] = {
    getNotesFromDb(GetNotesDto(notesReq.userId))
      .to[List]
      .transact(transactor)
      .map(notes => Option(RespGetNotes(notesReq.requestId, notes)))
  }

  final def getNoteByIdFromDatabase(noteReq: GetNoteByIdReq)  :F[Option[RespGetNoteById]] = {
    getNoteById(GetNoteByIdDto(noteReq.userId, noteReq.noteId))
      .to[List]
      .map(_.headOption)
      .transact(transactor)
      .map(_.map { note =>
        RespGetNoteById(note.title, note.body)
      })
  }

  override def saveNoteToDb(noteReq: SaveNoteReq): F[Int] = {
    saveNote(SaveRequestDto(noteReq.userId,noteReq.title,noteReq.note))
      .transact(transactor)

  }
}
object NotesRepository {

  private def  getNotesFromDb(noteReq :GetNotesDto): doobie.Query0[RespGetNoteById] = {
    sql"""select title,notes from notes where user_id = ${noteReq.userId}"""
      .query[RespGetNoteById]
  }

  private def getNoteById(noteReq: GetNoteByIdDto ): doobie.Query0[RespGetNoteById] = {
    sql"""select title,notes from notes where
         user_id = ${noteReq.userId} and id = ${noteReq.noteId}"""
      .query[RespGetNoteById]
  }

  private def saveNote(postReqDto :SaveRequestDto): doobie.ConnectionIO[Int] = {
    sql"""
         insert into notes(title,notes,user_id) values(${postReqDto.title},${postReqDto.note},${postReqDto.userId})
       """.update.run
  }

}
