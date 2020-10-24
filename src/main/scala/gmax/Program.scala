package gmax

import cats.Monad
import cats.implicits._

import scala.util.Try

object Program extends App {

  trait Increment[F[_]] {
    def plusOne(i: Int): F[Int]
  }

  implicit object incTry extends Increment[Try] {
    def plusOne(i: Int): Try[Int] = Try(i + 1)
  }

  def program[F[_] : Monad](i: Int)(implicit I: Increment[F]): F[Int] = for {
    j <- I.plusOne(i)
    z <- if (j < 10000) program[F](j) else Monad[F].pure(j)
  } yield z

  println(program[Try](0))
}