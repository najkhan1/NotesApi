package com.najkhan.notesapi.databaseUtil

import cats.effect.IO
import com.najkhan.notesapi.config.{Config, DBConfig}
 import doobie.Transactor
import doobie.util.transactor.Transactor.Aux

object DbUtil {
  private val dbConfig: DBConfig = Config.config.DBConfig
  val transactor: Aux[IO, Unit] = Transactor.fromDriverManager[IO](
    driver = dbConfig.driver,
    url = dbConfig.url,
    user = dbConfig.user,
    password = dbConfig.password,
    logHandler = None
  )

}
