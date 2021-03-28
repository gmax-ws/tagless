package gmax

import cats.MonadError
import cats.effect.IO

import scala.util.Try

object Validation extends App {

  def validate[F[_], A](f: => A)(implicit M: MonadError[F, Throwable]): F[A] =
    Try(f).toEither match {
      case Right(a) => M.pure(a)
      case Left(error) => M.raiseError(error)
    }

  def program(a: => Int): IO[Int] = (for {
    result <- validate[IO, Int](a)
  } yield result)
    .handleErrorWith { error =>
      println(s"An error occurred while evaluating expression, $error")
      IO.raiseError(error)
    }

  println(program(5 / 2).unsafeRunSync())
  // print "An error occurred while evaluating expression java.lang.ArithmeticException: / by zero"
  program(5 / 0).unsafeRunSync()
}