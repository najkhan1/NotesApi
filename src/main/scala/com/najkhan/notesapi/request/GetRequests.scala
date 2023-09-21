package com.najkhan.notesapi.request

import com.najkhan.notesapi.request.RequestIdGenerator.generateRequestId

trait GetNotes
final case class GetNotesReq(userId :String, requestId :String = generateRequestId) extends GetNotes
final case class GetNoteByIdReq(userId :String, noteId: String, requestId :String = generateRequestId) extends GetNotes

