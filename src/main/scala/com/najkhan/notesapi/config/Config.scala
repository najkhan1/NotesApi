package com.najkhan.notesapi.config

import pureconfig._
import pureconfig.generic.auto._


case class DBConfig(
                   driver: String,
                   url: String,
                   user: String,
                   password: String,
                   logger: Option[String]
                   )
case class Config(
                 DBConfig: DBConfig
                 )


object Config {
  val config: Config = ConfigSource.resources("dev.conf").loadOrThrow[Config]


}
