import cats.effect.{IO, IOApp}
import com.najkhan.notesapi.NotesapiServer

object Main extends IOApp.Simple {

  val run = NotesapiServer.run[IO]
}
