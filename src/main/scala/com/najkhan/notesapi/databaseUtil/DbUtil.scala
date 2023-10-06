package com.najkhan.notesapi.databaseUtil

import cats.effect.kernel.Async
import com.najkhan.notesapi.config.{Config, DBConfig}
import doobie.Transactor
import doobie.util.transactor.Transactor.Aux

trait CreateConnection[F[_]] {
  def transactor : Aux[F, Unit]
}

class DbUtil[F[_] : Async] extends CreateConnection[F] {
  private val dbConfig: DBConfig = Config.config.DBConfig
  override def transactor: Aux[F, Unit] = Transactor.fromDriverManager[F](
    driver = dbConfig.driver,
    url = dbConfig.url,
    user = dbConfig.user,
    password = dbConfig.password,
    logHandler = None
  )
}

