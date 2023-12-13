package com.najkhan.notesapi.response

case class SaveResponse(returnFromDb: String)

object SaveResponse {
  def saveResult(dbReturn: Int) = if (dbReturn == 1) "Note successfully saved" else "Unable to save note to DB"
}
