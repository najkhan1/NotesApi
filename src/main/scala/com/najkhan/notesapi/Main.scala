import cats.effect.{IO, IOApp}
import com.najkhan.notesapi.NotesapiServer
import com.najkhan.notesapi.dbTest.TestDb

//package com.najkhan.notesapi
//
//import cats.effect.{IO, IOApp}
//


object Main extends IOApp.Simple {
  new TestDb
  val run = NotesapiServer.run[IO]
}
