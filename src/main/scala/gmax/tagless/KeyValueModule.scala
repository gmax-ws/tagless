package gmax.tagless

import cats.effect.IO

trait KeyValueStoreKV[F[_], K, V] {
  def get(key: K): F[Option[V]]

  def put(key: K, value: V): F[Unit]

  def delete(key: K): F[Boolean]

  def list: F[Seq[(K, V)]]

  def empty: F[Unit]
}

trait KeyValueStoreA[F[_]] extends KeyValueStoreKV[F, String, Int]

object KeyValueModule {
  def apply[F[_]](implicit
      kvs: KeyValueStoreA[F]
  ): KeyValueStoreA[F] = kvs
}

object KeyValueStoreInterpreter {
  private val store = scala.collection.mutable.Map[String, Int]()

  val keyValueStore: KeyValueStoreA[IO] =
    new KeyValueStoreA[IO] {
      def get(key: String): IO[Option[Int]] = IO.pure(store.get(key))

      def put(key: String, value: Int): IO[Unit] =
        IO.pure(store.put(key, value))

      def delete(key: String): IO[Boolean] =
        IO.pure(store.remove(key).isDefined)

      def list: IO[Seq[(String, Int)]] = IO.pure(store.toSeq)

      def empty: IO[Unit] = IO.pure(store.clear())
    }

}

