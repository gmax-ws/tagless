package gmax.module

import cats.effect.IO

trait KeyValueStore {
  val kvs: IO[KeyValueStore.Service.type] = IO.pure(KeyValueStore.Service)
}

object KeyValueStore extends KeyValueStore {

  sealed trait Service[F[_], K, V] {
    def get(key: K): F[Option[V]]

    def put(key: K, value: V): F[Option[V]]

    def delete(key: K): F[Boolean]

    def list: F[Seq[(K, V)]]

    def empty: F[Unit]
  }

  object Service extends KeyValueStore.Service[IO, String, Int] {
    private val store = scala.collection.mutable.Map[String, Int]()

    def get(key: String): IO[Option[Int]] =
      IO.pure(store.get(key))

    def put(key: String, value: Int): IO[Option[Int]] =
      IO.pure(store.put(key, value))

    def delete(key: String): IO[Boolean] =
      IO.pure(store.remove(key).isDefined)

    def list: IO[Seq[(String, Int)]] =
      IO.pure(store.toSeq)

    def empty: IO[Unit] =
      IO.pure(store.clear())
  }
}
