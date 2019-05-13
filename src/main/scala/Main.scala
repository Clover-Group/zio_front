package clover.tsp.front

import cats.effect._
import clover.tsp.front.config._
import clover.tsp.front.http.TodoService
import clover.tsp.front.repository._
import fs2.Stream.Compiler._
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.CORS
import pureconfig.generic.auto._
import scalaz.zio._
import scalaz.zio.blocking.Blocking
import scalaz.zio.clock.Clock
import scalaz.zio.console._
import scalaz.zio.interop.catz._
import scalaz.zio.scheduler.Scheduler

object Main extends App {

  type AppEnvironment = Clock with Console with Blocking with Repository
  type AppTask[A] = TaskR[AppEnvironment, A]

  override def run(args: List[String]): ZIO[Environment, Nothing, Int] =
    (for {
      cfg          <- ZIO.fromEither(pureconfig.loadConfig[Config])
      _            <- initDb(cfg.dbConfig)

      blockingEC   <- ZIO.environment[Blocking].flatMap(_.blocking.blockingExecutor).map(_.asEC)
      transactorR   = mkTransactor(cfg.dbConfig, Platform.executor.asEC, blockingEC)

      httpApp       = Router[AppTask](
                        "/todos" -> TodoService(s"${cfg.appConfig.baseUrl}/todos").service
                      ).orNotFound
      server        = ZIO.runtime[AppEnvironment].flatMap { implicit rts =>
                        BlazeServerBuilder[AppTask]
                          .bindHttp(cfg.appConfig.port, "0.0.0.0")
                          .withHttpApp(CORS(httpApp))
                          .serve
                          .compile[AppTask, AppTask, ExitCode]
                          .drain
                      }

      program      <- transactorR.use { transactor =>
                        server.provideSome[Environment] { base =>
                          new Clock with Console with Blocking with DoobieRepository {
                            override protected def xa: doobie.Transactor[Task] = transactor

                            override val scheduler: Scheduler.Service[Any] = base.scheduler
                            override val console: Console.Service[Any] = base.console
                            override val clock: Clock.Service[Any] = base.clock
                            override val blocking: Blocking.Service[Any] = base.blocking
                          }
                        }
                      }
    } yield program).foldM(err => putStrLn(s"Execution failed with: $err") *> ZIO.succeed(1), _ => ZIO.succeed(0))

}
