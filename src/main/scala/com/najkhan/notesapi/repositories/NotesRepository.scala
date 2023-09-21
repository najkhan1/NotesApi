package com.najkhan.notesapi.repositories

import com.najkhan.notesapi.Dto.{GetNoteByIdDto, GetNotesDto}
import com.najkhan.notesapi.response.RespGetNote
import doobie.implicits.toSqlInterpolator

object NotesRepository {
  def  getNotesFromDb(noteReq :GetNotesDto) = {
    sql"""select title,notes from notes where user_id = ${noteReq.userId}""".query[RespGetNote]
  }

  def getNoteById(noteReq: GetNoteByIdDto ) = {
    sql"""select title,notes from notes where user_id = ${noteReq.userId} and id = ${noteReq.noteId}""".query[RespGetNote]
  }

}
