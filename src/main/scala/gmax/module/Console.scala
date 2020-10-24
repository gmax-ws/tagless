package gmax.module

import cats.effect.IO

trait Console {
  val console: IO[Console.Service.type] = IO.pure(Console.Service)
}

object Console extends Console {

  sealed trait Service[F[_], A] {
    def putStr(text: A): F[Unit]

    def putStrLn(text: A): F[Unit]

    def readLn: F[A]
  }

  object Service extends Console.Service[IO, Any] {
    def putStrLn(str: Any): IO[Unit] = IO(println(str))

    def putStr(str: Any): IO[Unit] = IO(print(str))

    def readLn: IO[Any] = IO(scala.io.StdIn.readLine)
  }
}
