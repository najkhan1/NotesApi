package com.najkhan.notesapi.services

import com.najkhan.notesapi.repositories.NotesRepository
import com.najkhan.notesapi.request.{GetNoteByIdReq, GetNotesReq}
import com.najkhan.notesapi.response.{RespGetNoteById, RespGetNotes}


trait GetNotesService[F[_]] {
  def getNotes(getNotesReq: GetNotesReq) :F[Option[RespGetNotes]]
  def getNote(getNoteReq: GetNoteByIdReq) :F[Option[RespGetNoteById]]

}

class NotesService[F[_]](notesRepository: NotesRepository[F]) extends GetNotesService[F] {
  final def getNotes(getNotesReq: GetNotesReq): F[Option[RespGetNotes]] = {
    notesRepository.getNotesFromDatabase(getNotesReq)
  }

  final def getNote(getNoteReq: GetNoteByIdReq): F[Option[RespGetNoteById]] =
    notesRepository.getNoteByIdFromDatabase(getNoteReq)

}

