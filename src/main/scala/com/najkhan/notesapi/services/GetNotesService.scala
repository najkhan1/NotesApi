package com.najkhan.notesapi.services

import cats.Applicative
import cats.effect.IO
import cats.implicits.catsSyntaxApplicativeId
import com.najkhan.notesapi.Dto.{GetNoteByIdDto, GetNotesDto}
import com.najkhan.notesapi.databaseUtil.DbUtil
import com.najkhan.notesapi.repositories.NotesRepository.{getNoteById, getNotesFromDb}
import com.najkhan.notesapi.request.{GetNoteByIdReq, GetNotesReq}
import com.najkhan.notesapi.response.{RespGetNoteById, RespGetNotes}
import doobie.WeakAsync.doobieWeakAsyncForAsync
import doobie.implicits._
import doobie.util.transactor.Transactor.Aux


trait GetNotesService[F[_]] {


  def getNotes(getNotesReq: GetNotesReq) :F[IO[RespGetNotes]]
  def getNote(getNoteReq: GetNoteByIdReq) :F[IO[RespGetNoteById]]

//  private[services] def getNotesFromDatabase(notesReq: GetNotesReq): F[_]
//
//  private[services] def getNoteByIdFromDatabase(noteReq: GetNoteByIdReq): F[_]

}

object GetNotesService {

  val transactor: Aux[IO, Unit] = DbUtil.transactor

  def impl[F[_] : Applicative ]: GetNotesService[F] =
    new GetNotesService[F] {
    final def getNotes(getNotesReq: GetNotesReq) :F[IO[RespGetNotes]] =
      getNotesFromDatabase(getNotesReq).pure[F]
    final def getNote(getNoteReq: GetNoteByIdReq) :F[IO[RespGetNoteById]] =
      getNoteByIdFromDatabase(getNoteReq).pure[F]
  }

  private[services] def getNotesFromDatabase(notesReq :GetNotesReq): IO[RespGetNotes] = {
      getNotesFromDb(GetNotesDto(notesReq.userId))
        .to[List]
        .transact(transactor)
        .map(notes => RespGetNotes(notesReq.requestId, notes))
  }

  private[services] def getNoteByIdFromDatabase(noteReq: GetNoteByIdReq): IO[RespGetNoteById] = {
    getNoteById(GetNoteByIdDto(noteReq.userId, noteReq.noteId))
      .to[List]
      .transact(transactor)
      .map{ note =>
        val aNote = note.head
        RespGetNoteById(aNote.title, aNote.body)
      }

  }

//
//  class GetNotesServiceImpl[F[_] ] extends GetNotesService[cats.effect.IO] {
//
//    val transactor: Aux[IO, Unit] = DbUtil.transactor
//
//    override def getNotes(getNotesReq: GetNotesReq): IO[RespGetNotes] =
//      getNotesFromDatabase(getNotesReq)
//
//    override def getNote(getNoteReq: GetNoteByIdReq): IO[RespGetNoteById] =
//      getNoteByIdFromDatabase(getNoteReq)
//
//    private[services] def getNotesFromDatabase(notesReq: GetNotesReq) = {
//      getNotesFromDb(GetNotesDto(notesReq.userId))
//        .to[List]
//        .transact(transactor)
//        .map(notes => RespGetNotes(notesReq.requestId, notes))
//    }
//
//    private[services] def getNoteByIdFromDatabase(noteReq: GetNoteByIdReq) = {
//      getNoteById(GetNoteByIdDto(noteReq.userId, noteReq.noteId))
//        .to[List]
//        .transact(transactor)
//        .map { note =>
//          val aNote = note.head
//          RespGetNoteById(aNote.title, aNote.body)
//        }
//    }
//  }



}