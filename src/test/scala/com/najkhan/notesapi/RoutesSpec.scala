package com.najkhan.notesapi

import com.najkhan.notesapi.RoutesFixture.{checkGet, checkPost, notesGetRoutes, notesSaveRoutes, repIdOne, repMultiNotes, repNoNote, repNoNotes, saveNotesReq, saveNotesReqBadRequest, saveResponseFailure, saveResponseSuccess}
import io.circe.Json
import io.circe.generic.auto.exportEncoder
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.circe.jsonDecoder
import org.http4s.implicits.http4sLiteralsSyntax
import org.http4s.{Method, Request, Status}
import org.scalatest.freespec.AnyFreeSpec

import java.io.{ByteArrayOutputStream, ObjectOutputStream}



class RoutesSpec extends AnyFreeSpec {

  def serialise[A](value: A): Seq[Byte] = {
    val stream: ByteArrayOutputStream = new ByteArrayOutputStream()
    val oos = new ObjectOutputStream(stream)
    oos.writeObject(value)
    oos.close()
    stream.toByteArray.toSeq
  }


  "when user has saved notes" - {
    "return note by id if user has notes" in {
      val req = notesGetRoutes.getNotesRoutes.run(
        Request(Method.GET, uri = uri"/1/1")
      )
      val res = checkGet[Json](req.value,Status.Ok,Some(repIdOne))
      assert(res,true)
    }

    "return notes if user has notes" in {
      val req = notesGetRoutes.getNotesRoutes.run(
        Request(Method.GET, uri = uri"/getnotes/1")
      )
      val res = checkGet[Json](req.value,Status.Ok,Some(repMultiNotes))
      assert(res,true)
    }
  }
  "When user has no notes" - {
    "When user does not have saved note with requested id" in {
      val req = notesGetRoutes.getNotesRoutes.run(
        Request(Method.GET, uri = uri"/1/2")
      )
      val res = checkGet[Json](req.value,Status.Ok,Some(repNoNote))
      assert(res,true)
    }
    "When user does not have any saved note" in {
      val req = notesGetRoutes.getNotesRoutes.run(
        Request(Method.GET, uri = uri"/getnotes/2")
      )
      val res = checkGet[Json](req.value,Status.Ok,Some(repNoNotes))
      assert(res,true)
    }
  }
  "Save notes" - {
    "return success if successfully saved" in {
      val req = notesSaveRoutes.savePostNotesRoutes.orNotFound.run(
        Request(Method.POST, uri = uri"savenote").withEntity(saveNotesReq)
      )
      val res = checkPost[Json](req,Status.Ok,Some(saveResponseSuccess))
      assert(res,true)
    }
    "return failure if not successfully saved" in {
      val req = notesSaveRoutes.savePostNotesRoutes.orNotFound.run(
        Request(Method.POST, uri = uri"savenote").withEntity(saveNotesReqBadRequest)
      )
      val res = checkPost[Json](req,Status.Ok,Some(saveResponseFailure))
      assert(res,true)
    }
  }
}
