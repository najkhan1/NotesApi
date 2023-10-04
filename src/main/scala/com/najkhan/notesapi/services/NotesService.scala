package com.najkhan.notesapi.services

import cats.effect.IO
import com.najkhan.notesapi.Dto.{GetNoteByIdDto, GetNotesDto, SaveRequestDto}
import com.najkhan.notesapi.databaseUtil.DbUtil
import com.najkhan.notesapi.repositories.NotesRepository.{getNoteById, getNotesFromDb, saveNote}
import com.najkhan.notesapi.request.{GetNoteByIdReq, GetNotesReq, SaveNotReq}
import com.najkhan.notesapi.response.{RespGetNoteById, RespGetNotes}
import doobie.WeakAsync.doobieWeakAsyncForAsync
import doobie.implicits._
import doobie.util.transactor.Transactor.Aux


trait GetNotesService[F[_]] {


  def getNotes(getNotesReq: GetNotesReq) :IO[RespGetNotes]
  def getNote(getNoteReq: GetNoteByIdReq) :IO[RespGetNoteById]
  def saveNote(saveNoteReq: SaveNotReq): IO[Int]

//  private[services] def getNotesFromDatabase(notesReq: GetNotesReq): F[_]
//
//  private[services] def getNoteByIdFromDatabase(noteReq: GetNoteByIdReq): F[_]

}

object GetNotesService {

  val transactor: Aux[IO, Unit] = DbUtil.transactor

  def impl[F[_]]: GetNotesService[F] =
    new GetNotesService[F] {
    final def getNotes(getNotesReq: GetNotesReq) :IO[RespGetNotes] =
      getNotesFromDatabase(getNotesReq)
    final def getNote(getNoteReq: GetNoteByIdReq) :IO[RespGetNoteById]=
      getNoteByIdFromDatabase(getNoteReq)
    final def saveNote(saveNoteReq: SaveNotReq): IO[Int] =
      saveNotesToDB(saveNoteReq)
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

  private[services] def saveNotesToDB(saveNoteRequest: SaveNotReq) = {
    saveNote(SaveRequestDto(saveNoteRequest.userId, saveNoteRequest.title, saveNoteRequest.note))
      .transact(transactor)
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