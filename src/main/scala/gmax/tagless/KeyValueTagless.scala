package gmax.tagless

import cats.Monad
import cats.effect.IO
import cats.implicits._

object KeyValueStorePrograms {
  def programContextBound[F[_]: KeyValueStoreA: ConsoleA: Monad]: F[Unit] =
    for {
      _ <- KeyValueModule[F].put("john", 58)
      _ <- KeyValueModule[F].put("elvis", 63)
      _ <- KeyValueModule[F].put("gregory", 89)
      all <- KeyValueModule[F].list
      _ <- ConsoleModule[F].putStrLn(all)
      v <- KeyValueModule[F].get("john")
      _ <- ConsoleModule[F].putStrLn(v)
      d <- KeyValueModule[F].delete("elvis")
      _ <- ConsoleModule[F].putStrLn(d)
      all <- KeyValueModule[F].list
      _ <- ConsoleModule[F].putStrLn(all)
      _ <- KeyValueModule[F].empty
      all <- KeyValueModule[F].list
      _ <- ConsoleModule[F].putStrLn(all)
      _ <- ConsoleModule[F].putStr("Press ENTER to exit: ")
      _ <- ConsoleModule[F].readLn
    } yield ()

  def programImplicitParams[F[_]: Monad](implicit
      KVS: KeyValueStoreA[F],
      C: ConsoleA[F]
  ): F[Unit] =
    for {
      _ <- KVS.put("john", 58)
      _ <- KVS.put("elvis", 63)
      _ <- KVS.put("gregory", 89)
      all <- KVS.list
      _ <- C.putStrLn(all)
      v <- KVS.get("john")
      _ <- C.putStrLn(v)
      d <- KVS.delete("elvis")
      _ <- C.putStrLn(d)
      all <- KVS.list
      _ <- C.putStrLn(all)
      _ <- KVS.empty
      all <- KVS.list
      _ <- C.putStrLn(all)
      _ <- C.putStr("Press ENTER to exit: ")
      _ <- C.readLn
    } yield ()
}

object KeyValueTagless extends App {

  implicit lazy val stdConsole: ConsoleA[IO] = ConsoleInterpreter.stdConsole
  implicit lazy val keyValueStore: KeyValueStoreA[IO] =
    KeyValueStoreInterpreter.keyValueStore

  lazy val myProgram: IO[Unit] =
    for {
      _ <- KeyValueStorePrograms.programContextBound[IO]
      _ <- KeyValueStorePrograms.programImplicitParams[IO]
    } yield ()

  myProgram.unsafeRunSync()
}
