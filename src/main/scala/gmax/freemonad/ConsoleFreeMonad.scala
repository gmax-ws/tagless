package gmax.freemonad

import cats.free.Free
import cats.{Id, ~>}

sealed trait ConsoleOp[+A]

case class PrintLn(text: String) extends ConsoleOp[Unit]

case class ReadLn() extends ConsoleOp[String]

object Console {
  type Console[A] = Free[ConsoleOp, A]

  def readLn: Console[String] =
    Free.liftF(ReadLn())

  def printLn(text: String): Console[Unit] =
    Free.liftF(PrintLn(text))
}

object StdConsoleInterpreter {

  import Console._

  def apply[A](f: Console[A]): Id[A] = f.foldMap(Compiler)

  object Compiler extends (ConsoleOp ~> Id) {
    override def apply[A](fa: ConsoleOp[A]): Id[A] =
      fa match {
        case PrintLn(text) ⇒ println(text)
        case ReadLn() ⇒ scala.io.StdIn.readLine()
      }
  }

}

object ConsoleFreeMonad {

  import Console._

  lazy val program: Console[String] = {
    for {
      _ <- printLn("Please tell me your name (empty to exit):")
      greeting = "Hello"
      name <- readLn
      _ <- printLn(s"$greeting $name")
    } yield name
  }

  def main(args: Array[String]): Unit = {
    StdConsoleInterpreter(program)
  }
}