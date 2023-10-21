import cats.effect.{IO, IOApp}
import com.najkhan.notesapi.NotesapiServer

object Main extends IOApp.Simple {
  val run: IO[Nothing] = NotesapiServer.run[IO]
}
