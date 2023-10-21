package com.najkhan.notesapi

import cats.effect.IO
import com.najkhan.notesapi.databaseUtil.DbUtil
import com.najkhan.notesapi.repositories.NotesRepository
import com.najkhan.notesapi.response.RespGetNoteById
import com.najkhan.notesapi.services.NotesService
import org.scalatest.PrivateMethodTester
import org.scalatest.freespec.AnyFreeSpec


class notesApiRoutesSpec extends AnyFreeSpec with PrivateMethodTester {

  "optionator" - {
    val transactor = new DbUtil[IO]().transactor
    val repository = new NotesRepository[IO](transactor)
    val service = new NotesService[IO](repository)
    val routes = new NotesApiRoutes[IO](service)
    "when input is None" - {
      "return default value of 0 if input type is Option[Int]" in {
        val methodUnderTest = PrivateMethod[Int](Symbol("optionator"))
        val res = routes invokePrivate  methodUnderTest(0, None)
        assert(res == 0)
      }
      "return default empty string if input type is Option[String] " in {
        val methodUnderTest = PrivateMethod[String](Symbol("optionator"))
        val res = routes invokePrivate methodUnderTest("", None)
        assert(res == "")
      }

      "return default empty RespGetNoteById case class if input type is Option[RespGetNoteById] " in {
        val methodUnderTest = PrivateMethod[RespGetNoteById](Symbol("optionator"))
        val res = routes invokePrivate methodUnderTest(RespGetNoteById("",""), None)
        assert(res == RespGetNoteById("",""))
      }
    }
    "when input is Some(value)" - {
      "return int value in option if option is not None and input type is Option[Int]" in {
        val methodUnderTest = PrivateMethod[Int](Symbol("optionator"))
        val res = routes invokePrivate methodUnderTest(1, None)
        assert(res == 1)
      }
      "return String value in option if option is not None and input type is Option[String]" in {
        val methodUnderTest = PrivateMethod[String](Symbol("optionator"))
        val res = routes invokePrivate methodUnderTest("some string", None)
        assert(res == "some string")
      }
      "return RespGetNoteById value in option if option is not None and input type is Option[RespGetNoteById]" in {
        val methodUnderTest = PrivateMethod[RespGetNoteById](Symbol("optionator"))
        val res = routes invokePrivate methodUnderTest(RespGetNoteById("some title","some string"), None)
        assert(res == RespGetNoteById("some title","some string"))
      }
    }
  }

}
