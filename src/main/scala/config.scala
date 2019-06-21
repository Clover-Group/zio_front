package clover.tsp.front

import doobie.hikari._
import doobie.util.transactor.Transactor
import org.flywaydb.core.Flyway
import zio._
import zio.interop.catz._

import scala.concurrent.ExecutionContext
import cats.effect.Blocker

object config {

  final case class Config(
    appConfig: AppConfig,
    dbConfig: DBConfig
  )

  final case class AppConfig(
    host: String,
    port: Int,
    baseUrl: String
  )

  final case class DBConfig(
    url: String,
    driver: String,
    user: String,
    password: String
  )

  def initDb(cfg: DBConfig): Task[Unit] =
    ZIO.effect {
      val fw = Flyway
        .configure()
        .dataSource(cfg.url, cfg.user, cfg.password)
        .load()
      fw.migrate()
    }.unit

  def mkTransactor(
    cfg: DBConfig,
    connectEC: ExecutionContext,
    transactEC: Blocker
  ): Managed[Throwable, Transactor[Task]] = {
    val xa =
      HikariTransactor.newHikariTransactor[Task](cfg.driver, cfg.url, cfg.user, cfg.password, connectEC, transactEC)

    val res = xa.allocated.map {
      case (transactor, cleanupM) =>
        Reservation(ZIO.succeed(transactor), cleanupM.orDie)
    }.uninterruptible

    Managed(res)
  }

}
