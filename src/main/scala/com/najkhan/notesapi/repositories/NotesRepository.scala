package com.najkhan.notesapi.repositories

import com.najkhan.notesapi.Dto.{GetNoteByIdDto, GetNotesDto, SaveRequestDto}
import com.najkhan.notesapi.response.RespGetNoteById
import doobie.implicits.toSqlInterpolator

object NotesRepository {
  def  getNotesFromDb(noteReq :GetNotesDto) = {
    sql"""select title,notes from notes where user_id = ${noteReq.userId}""".query[RespGetNoteById]
  }

  def getNoteById(noteReq: GetNoteByIdDto ) = {
    sql"""select title,notes from notes where user_id = ${noteReq.userId} and id = ${noteReq.noteId}""".query[RespGetNoteById]
  }

  def saveNote(saveNoteReq: SaveRequestDto) = {
    sql"""
         insert into notes(title, notes, user_id)
         values(${saveNoteReq.title}, ${saveNoteReq.note}, ${saveNoteReq.userId})
       """.update.run
  }

}
