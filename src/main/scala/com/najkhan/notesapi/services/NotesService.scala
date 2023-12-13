package com.najkhan.notesapi.services

import com.najkhan.notesapi.repositories.Repository
import com.najkhan.notesapi.request.{GetNoteByIdReq, GetNotesReq, SaveNoteReq}
import com.najkhan.notesapi.response.{RespGetNoteById, RespGetNotes}


trait GetNotesService[F[_]] {
  def getNotes(getNotesReq: GetNotesReq) :F[Option[RespGetNotes]]
  def getNote(getNoteReq: GetNoteByIdReq) :F[Option[RespGetNoteById]]
  def saveNote(saveNoteReq: SaveNoteReq): F[Int]

}

class NotesService[F[_]](notesRepository: Repository[F]) extends GetNotesService[F] {
  final def getNotes(getNotesReq: GetNotesReq): F[Option[RespGetNotes]] = {
    notesRepository.getNotesFromDatabase(getNotesReq)
  }
  final def getNote(getNoteReq: GetNoteByIdReq): F[Option[RespGetNoteById]] =
    notesRepository.getNoteByIdFromDatabase(getNoteReq)
  def saveNote(saveNoteReq: SaveNoteReq): F[Int] = notesRepository.saveNoteToDb(saveNoteReq)

}