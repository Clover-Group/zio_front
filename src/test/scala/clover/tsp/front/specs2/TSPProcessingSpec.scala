package clover.tsp.front.specs2

import java.nio.file.Paths

import clover.tsp.front.repository.implementations.PostgresRepository._
import clover.tsp.front.repository.implementations.PostgresRepository

import com.dimafeng.testcontainers.PostgreSQLContainer
import clover.tsp.front.domain.DBItem
import clover.tsp.front.http.DBService
import io.circe.literal._
import io.circe.parser._
import org.specs2.specification.core.SpecStructure
import org.http4s.implicits._
import org.http4s.dsl.Http4sDsl
import zio.{ IO, Ref, Task, UIO, ZIO }
import zio.interop.catz._
import zio.DefaultRuntime
import doobie.implicits._

import scala.io.Source
import java.io.{ File, FileInputStream }
import java.nio.charset.StandardCharsets

import clover.tsp.front.repository.implementations
import clover.tsp.front.repository.interfaces.Repository
import doobie.util.transactor.Transactor
import io.circe.Json
import org.http4s.{ Method, Status }

class TSPProcessingSpec extends HTTPSpec2 {
  import TSPProcessingSpec._
  import TSPProcessingSpec.dbInfoService._

  val app                        = dbInfoService.service.orNotFound
  val dsl: Http4sDsl[TSPTaskDTO] = Http4sDsl[TSPTaskDTO]

  val currentPath     = Paths.get(".").toAbsolutePath
  val filePath        = s"$currentPath/assets/json/req0.txt"
  val pathToKafkaJson = s"$currentPath/assets/json/loco/kafka.json"
  val pathToPgJson    = s"$currentPath/assets/json/postgres.json"

  override def is: SpecStructure =
    s2"""
        TSP REST Service should
          status should be Ok                    $t1
          retrieve info about DB with status OK  $t2
          should call kafka service              $t3
          should call Postgres service           $t4
      """

  def closeStream(is: FileInputStream) = UIO(is.close())

  def readAll(fis: FileInputStream, len: Long): Array[Byte] = {
    val content: Array[Byte] = Array.ofDim(len.toInt)
    fis.read(content)
    content
  }

  def convertBytes(is: FileInputStream, len: Long): Task[String] =
    Task.effect(new String(readAll(is, len), StandardCharsets.UTF_8))

  def t1 =
    runWithEnv(
      for {

        buffer   <- ZIO.effect(Source.fromFile(filePath)).mapError(_ => new Throwable("Fail to open the file"))
        jsonData = buffer.mkString
        _        <- ZIO.effect(buffer.close).mapError(_ => new Throwable("Fail to close the file"))
        parseResult <- ZIO
                        .effect(parse(jsonData).getOrElse(Json.Null))
                        .mapError(_ => new Throwable("JSON parse failed"))
        req      = request[TSPTaskDTO](Method.POST, "/").withEntity(json"""$parseResult""")
        res      <- ZIO.effect(app.run(req)).mapError(_ => new Throwable("HTTP effect failed"))
        response <- res
        status   = response.status
      } yield status === Status.Ok
    )

  def t2 =
    runWithEnv(
      for {
        file <- Task(new File(filePath))
        len  = file.length
        jsonData <- Task(new FileInputStream(file))
                     .bracket(closeStream)(convertBytes(_, len))
                     .mapError(_ => new Throwable("Error when working with file"))
        finalJson = jsonData
        parseResult <- ZIO
                        .effect(parse(finalJson).getOrElse(Json.Null))
                        .mapError(_ => new Throwable("JSON parse failed"))
        req      = request[TSPTaskDTO](Method.POST, "/").withEntity(json"""$parseResult""")
        res      <- ZIO.effect(app.run(req)).mapError(_ => new Throwable("HTTP effect failed"))
        response <- res
        status   = response.status
      } yield status.equals(Status.Ok)
    )

  def t3 =
    runWithEnv(
      for {
        file <- Task(new File(pathToKafkaJson))
        len  = file.length
        jsonData <- Task(new FileInputStream(file))
                     .bracket(closeStream)(convertBytes(_, len))
                     .mapError(error => new Throwable(s"Error when working with file $error"))
        finalJson = jsonData
        parseResult <- ZIO
                        .effect(parse(finalJson).getOrElse(Json.Null))
                        .mapError(_ => new Throwable("JSON parse failed"))
        req      = request[TSPTaskDTO](Method.POST, "/").withEntity(json"""$parseResult""")
        res      <- ZIO.effect(app.run(req)).mapError(_ => new Throwable("HTTP effect failed"))
        response <- res
        status   = response.status
      } yield status.equals(Status.Ok)
    )

  def t4 =
    runWithEnv(
      for {
        file <- Task(new File(pathToPgJson))
        len  = file.length
        jsonData <- Task(new FileInputStream(file))
                     .bracket(closeStream)(convertBytes(_, len))
                     .mapError(error => new Throwable(s"Error when working with file $error"))
        finalJson = jsonData
        parseResult <- ZIO
                        .effect(parse(finalJson).getOrElse(Json.Null))
                        .mapError(_ => new Throwable("JSON parse failed"))
         container    <- ZIO(PostgreSQLContainer())
         _            <- IO.effectTotal(container.start())
         xa           = getTransactor(container)
        pgRepository = PostgresRepository(xa)
        _            <- pgRepository.createTable(sql"""CREATE TABLE IF NOT EXISTS test_table (
                                                      from_ts BIGINT,
                                                      to_ts BIGINT
                                                      )""")
        req = request[TSPTaskDTO](Method.POST, "/").withEntity(json"""$parseResult""")
        res <- ZIO
                .effect(DBService[Repository]("", pgRepository).service.orNotFound.run(req))
                .mapError(_ => new Throwable("HTTP effect failed"))
        response <- res
        status   = response.status
      } yield status.equals(Status.Ok)
    )
}

object TSPProcessingSpec extends DefaultRuntime {

  val xa: Transactor[Task] = Transactor.fromDriverManager[Task](
    "org.postgresql.Driver",
    "jdbc:postgresql://localhost:5434/test_db",
    "test_user",
    "test_password"
  )
  val pgRepository: PostgresRepository     = PostgresRepository(xa)
  val dbInfoService: DBService[Repository] = DBService[Repository]("", pgRepository)

  val mkEnv: UIO[Repository] =
    for {
      store   <- Ref.make(DBItem("some data"))
      counter <- Ref.make(0L)
      repo    = implementations.SimpleRepository(store, counter)
      env = new Repository {
        override val dbInfoRepository: Repository.SimpleService[Any] = repo
        override val todoRepository: Repository.Service[Any]         = null
      }
    } yield env

  def runWithEnv[E, A](task: ZIO[Repository, E, A]): A =
    unsafeRun[E, A](mkEnv.flatMap(env => task.provide(env)))

}
