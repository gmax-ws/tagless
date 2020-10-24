package gmax.freemonad

import cats.data.State
import cats.free.Free
import cats.{Eval, Id, ~>}

import scala.collection.immutable._

// DSL domain specific language (commands)
sealed trait KVStoreA[A]

case class Put[T](key: String, value: T) extends KVStoreA[Unit]

case class Get[T](key: String) extends KVStoreA[Option[T]]

case class Delete(key: String) extends KVStoreA[Unit]

object FreeMonad {

  type KVStore[A] = Free[KVStoreA, A]

  // Put returns nothing (i.e. Unit).
  def put[T](key: String, value: T): KVStore[Unit] =
    Free.liftF[KVStoreA, Unit](Put[T](key, value))

  // Get returns a T value.
  def get[T](key: String): KVStore[Option[T]] =
    Free.liftF[KVStoreA, Option[T]](Get[T](key))

  // Delete returns nothing (i.e. Unit).
  def delete(key: String): KVStore[Unit] =
    Free.liftF(Delete(key))

  // Update composes get and set, and returns nothing.
  def update[T](key: String, f: T => T): KVStore[Unit] =
    for {
      vMaybe <- get[T](key)
      _ <- vMaybe.map(v => put[T](key, f(v))).getOrElse(Free.pure(()))
    } yield ()

  def program: KVStore[Option[Int]] =
    for {
      _ <- put("wild-cats", 2)
      _ <- update[Int]("wild-cats", _ + 12)
      _ <- put("tame-cats", 5)
      n <- get[Int]("wild-cats")
      _ <- delete("tame-cats")
    } yield n

  // the program will crash if a key is not found,
  // or if a type is incorrectly specified.
  def impureCompiler: KVStoreA ~> Id = new (KVStoreA ~> Id) {

    // a very simple (and imprecise) key-value store
    val kvs = scala.collection.mutable.Map.empty[String, Any]

    def apply[A](fa: KVStoreA[A]): Id[A] = fa match {
      case Put(key, value) =>
        println(s"put($key, $value)")
        kvs(key) = value
        ()
      case Get(key) =>
        println(s"get($key)")
        kvs.get(key)
      case Delete(key) =>
        println(s"delete($key)")
        kvs.remove(key)
        ()
    }
  }

  type MapKVStore = Map[String, Any]
  type KVStoreState[A] = State[MapKVStore, A]

  def pureCompiler: KVStoreA ~> KVStoreState = new (KVStoreA ~> KVStoreState) {
    def apply[A](fa: KVStoreA[A]): KVStoreState[A] =
      fa match {
        case Put(key, value) => State.modify(_.updated(key, value))
        case Get(key) => State.inspect(_.get(key))
        case Delete(key) => State.modify(_.-(key))
      }
  }

  val result: Option[Int] = program.foldMap(impureCompiler)
  val result2: Eval[(MapKVStore, Option[Int])] = program.foldMap(pureCompiler)
    .run(Map.empty[String, Option[Int]])

  def main(args: Array[String]): Unit = {
    println(result)
    println(result2.value._2)
  }
}
