package com.najkhan.notesapi

import cats.effect.{IO, IOApp}

object Main extends IOApp.Simple {
  val run = NotesapiServer.run[IO]
}
