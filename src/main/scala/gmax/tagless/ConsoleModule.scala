package gmax.tagless

import cats.effect.IO

trait Console[F[_], A] {
  def putStr(text: A): F[Unit]

  def putStrLn(text: A): F[Unit]

  def readLn: F[A]
}

trait ConsoleA[F[_]] extends Console[F, Any]

object ConsoleModule {
  def apply[F[_]](implicit
      console: ConsoleA[F]
  ): ConsoleA[F] = console
}

object ConsoleInterpreter {
  val stdConsole: ConsoleA[IO] = new ConsoleA[IO] {
    def putStrLn(str: Any): IO[Unit] = IO(println(str))
    def putStr(str: Any): IO[Unit] = IO(print(str))
    def readLn: IO[Any] = IO(scala.io.StdIn.readLine)
  }
}
