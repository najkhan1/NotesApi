package com.najkhan.notesapi.response

import com.najkhan.notesapi.services.GetNotesService
import io.circe.{Encoder, Json}
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf

sealed trait Resp
final case class RespGetNotes(requestId :String, notes : List[RespGetNote]) extends Resp

final case class RespGetNote(title :String, body :String) extends Resp


object Resp {

  def apply[F[_]](implicit ev: GetNotesService[F]): GetNotesService[F] = ev

  implicit val noteEncoder: Encoder[RespGetNote] = new Encoder[RespGetNote] {
    final def apply(a :RespGetNote) :Json = Json.obj(
      ("Title", Json.fromString(a.title)),
      ("Note", Json.fromString(a.body))
    )
  }
  implicit val respGetNotesEncoder: Encoder[RespGetNotes] = new Encoder[RespGetNotes] {
    final def apply(a: RespGetNotes): Json = Json.obj (
      ("notes", Json.fromValues(a.notes.map(noteEncoder.apply)))
    )
  }
  implicit def noteEntityEncoder[F[_]]: EntityEncoder[F, RespGetNote] = jsonEncoderOf[F, RespGetNote]
  implicit def respGetNotesEntityEncoder[F[_]]: EntityEncoder[F, RespGetNotes] = jsonEncoderOf[F, RespGetNotes]

}