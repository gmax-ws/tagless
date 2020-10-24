package gmax.tagless

import cats.Monad
import cats.effect.IO
import cats.implicits._

object ConsolePrograms {
  def programContextBound[F[_]: Monad: ConsoleA]: F[Unit] =
    for {
      _ <- ConsoleModule[F].putStrLn("Enter your name:")
      n <- ConsoleModule[F].readLn
      _ <- ConsoleModule[F].putStrLn(s"Hello $n!")
    } yield ()

  def programImplicitParams[F[_]: Monad](implicit C: ConsoleA[F]): F[Unit] =
    for {
      _ <- C.putStrLn("Enter your name:")
      n <- C.readLn
      _ <- C.putStrLn(s"Hello $n!")
    } yield ()
}

object TaglessFinal extends App {

  implicit lazy val stdConsole: ConsoleA[IO] = ConsoleInterpreter.stdConsole

  lazy val myProgram: IO[Unit] =
    for {
      _ <- ConsolePrograms.programContextBound[IO]
      _ <- ConsolePrograms.programImplicitParams[IO]
    } yield ()

  myProgram.unsafeRunSync()
}
