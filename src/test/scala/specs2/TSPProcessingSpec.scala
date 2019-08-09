package clover.tsp.front.specs2

import java.nio.file.Paths

import clover.tsp.front.domain.DBItem
import clover.tsp.front.http.DBService
import io.circe.literal._
import io.circe.parser._
import org.specs2.specification.core.SpecStructure
import clover.tsp.front.repository.{ DBInfoRepository, Repository }
import org.http4s.implicits._
import org.http4s.dsl.Http4sDsl
import zio.{ Ref, Task, UIO, ZIO }
import zio.interop.catz._
import zio.DefaultRuntime
import io.circe.generic.auto._

import scala.io.Source
import java.io.{ File, FileInputStream }
import java.nio.charset.StandardCharsets

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

  override def is: SpecStructure =
    s2"""
        TSP REST Service should
          retrieve info about DB                 $t1
          status should be Ok                    $t2
          retrieve info about DB with status OK  $t3
          should invoke kafka service            $t4
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

        file <- Task(new File(filePath))
        len  = file.length

        buffer   <- ZIO.effect(Source.fromFile(filePath)).mapError(_ => new Throwable("Fail to open the file"))
        jsonData = buffer.mkString
        _        <- ZIO.effect(buffer.close).mapError(_ => new Throwable("Fail to close the file"))
        parseResult <- ZIO
                        .effect(parse(jsonData).getOrElse(Json.Null))
                        .mapError(_ => new Throwable("JSON parse failed"))
        req          = request[TSPTaskDTO](Method.POST, "/").withEntity(json"""$parseResult""")
        res          <- ZIO.effect(app.run(req)).mapError(_ => new Throwable("HTTP effect failed"))
        response     <- res
        responseBody <- response.as[DBItem]

      } yield responseBody === DBItem("some data")
    )
  def t2 =
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

  def t3 =
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
        req          = request[TSPTaskDTO](Method.POST, "/").withEntity(json"""$parseResult""")
        res          <- ZIO.effect(app.run(req)).mapError(_ => new Throwable("HTTP effect failed"))
        response     <- res
        responseBody <- response.as[DBItem]
        status       = response.status
      } yield (responseBody, status).equals((DBItem("some data"), Status.Ok))
    )

  def t4 =
    runWithEnv(
      for {
        file <- Task(new File(pathToKafkaJson))
        len  = file.length
        jsonData <- Task(new FileInputStream(file))
                     .bracket(closeStream)(convertBytes(_, len))
                     .mapError(_ => new Throwable("Error when working with file"))
        finalJson = jsonData
        parseResult <- ZIO
                        .effect(parse(finalJson).getOrElse(Json.Null))
                        .mapError(_ => new Throwable("JSON parse failed"))
        req          = request[TSPTaskDTO](Method.POST, "/").withEntity(json"""$parseResult""")
        res          <- ZIO.effect(app.run(req)).mapError(_ => new Throwable("HTTP effect failed"))
        response     <- res
        responseBody <- response.as[DBItem]
        status       = response.status
      } yield (responseBody, status).equals((DBItem("some data"), Status.Ok))
    )
}

object TSPProcessingSpec extends DefaultRuntime {

  val dbInfoService: DBService[Repository] = DBService[Repository]("")

  val mkEnv: UIO[Repository] =
    for {
      store   <- Ref.make(DBItem("some data"))
      counter <- Ref.make(0L)
      repo    = DBInfoRepository(store, counter)
      env = new Repository {
        override val dbInfoRepository: Repository.SimpleService[Any] = repo
        override val todoRepository: Repository.Service[Any]         = null
      }
    } yield env

  def runWithEnv[E, A](task: ZIO[Repository, E, A]): A =
    unsafeRun[E, A](mkEnv.flatMap(env => task.provide(env)))

}
