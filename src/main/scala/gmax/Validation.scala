package gmax

import cats.MonadError
import cats.effect.IO

import scala.util.Try

object Validation {
  def validate[F[_], A](f: => A)(implicit M: MonadError[F, Throwable]): F[A] =
    Try(f).toEither match {
      case Right(a) => M.pure(a)
      case Left(error) => M.raiseError(error)
    }
}

object ValidationMain extends App {
  val program = for {
    result <- Validation.validate[IO, Int](15 / 5)
  } yield result
  println(program.unsafeRunSync())

  val program2 = for {
    result <- Validation.validate[IO, Int](5 / 0)
  } yield result
  println(program2.unsafeRunSync())
}