package gmax.module

import cats.effect.IO

object ModuleTest extends App {

  type Modules = KeyValueStore with Console

  implicit object ModulesObject extends KeyValueStore with Console

  def program(implicit modules: Modules): IO[Unit] = {
    for {
      kvs <- modules.kvs
      _ <- kvs.put("john", 58)
      _ <- kvs.put("elvis", 63)
      _ <- kvs.put("gregory", 89)
      all <- kvs.list
      console <- modules.console
      _ <- console.putStrLn(all)
      v <- kvs.get("john")
      _ <- console.putStrLn(v)
      d <- kvs.delete("elvis")
      _ <- console.putStrLn(d)
      all <- kvs.list
      _ <- console.putStrLn(all)
      _ <- kvs.empty
      all <- kvs.list
      _ <- console.putStrLn(all)
      _ <- console.putStr("Press ENTER to exit: ")
      _ <- console.readLn
    } yield ()
  }

  program.unsafeRunSync
}
