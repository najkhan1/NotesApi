package com.najkhan.notesapi.errorHandling

trait UserError extends Exception

case class ParseError(message :String, input :String) extends UserError
