package com.najkhan.notesapi.errorHandling

import cats.ApplicativeError
import cats.data.{Kleisli, OptionT}
import cats.effect.Async
import cats.implicits._
import org.http4s.dsl.Http4sDsl
import org.http4s.{HttpRoutes, InvalidMessageBodyFailure, Request, Response}


object ErrorHandler {

  def apply[F[_], E <: Throwable](
                                   routes: HttpRoutes[F]
                                 )(handler: E => F[Response[F]])(implicit ev: ApplicativeError[F, E]): HttpRoutes[F] =
    Kleisli { req: Request[F] =>
      OptionT {
        routes.run(req).value.handleErrorWith { e =>
          handler(e).map(Option(_))
        }
      }
    }
}

trait HttpErrorHandler[F[_], E <: Throwable] {
  def handle(routes: HttpRoutes[F]): HttpRoutes[F]
}

class UserHttpErrorHandler[F[_] :Async]
  extends HttpErrorHandler[F, UserError]
    with Http4sDsl[F] {

  private val handler: Throwable => F[Response[F]] = {
    case ParseError(_, m) =>
      BadRequest(s"Invalid input: $m")
    case InvalidMessageBodyFailure(details,cause) => BadRequest(s"bad input ${cause.get} \n details: $details")
    case t => InternalServerError(s"Something bad happened: $t")
  }

  override def handle(routes: HttpRoutes[F]): HttpRoutes[F] =
    ErrorHandler(routes)(handler)
}
