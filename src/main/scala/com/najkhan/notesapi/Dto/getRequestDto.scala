package com.najkhan.notesapi.Dto

final case class GetNotesDto(userId :String)
final case class GetNoteByIdDto(userId :String, noteId :String)
