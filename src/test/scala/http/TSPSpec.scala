package clover.tsp.front.http

import java.nio.file.Paths

import clover.tsp.front.{ DBItem, HTTPSpec }
import clover.tsp.front.repository.Repository
import clover.tsp.front.repository.Repository.DBInfoRepository
import io.circe.literal._
import io.circe.generic.auto._
import io.circe.parser._
import org.http4s._
import org.http4s.implicits._
import org.http4s.dsl.Http4sDsl
import zio.{ DefaultRuntime, Ref, UIO, ZIO }
import zio.interop.catz._
import zio.{ DefaultRuntime }

import scala.io.Source

class TSPSpec extends HTTPSpec {

  import TSPSpec._
  import TSPSpec.dbInfoService._

  val app = dbInfoService.service.orNotFound

  val dsl: Http4sDsl[TSPTaskDTO] = Http4sDsl[TSPTaskDTO]

  describe("DB Service") {
    it("should retrieve object") {

      val currentPath = Paths.get(".").toAbsolutePath
      val filePath    = s"$currentPath/assets/json/req0.txt"
      val buffer      = Source.fromFile(filePath)
      val jsonData    = buffer.mkString
      buffer.close

      val expectedBody = Some(DBItem("some data"))

      parse(jsonData) match {
        case Left(_) => println("Invalid JSON :(")
        case Right(json) =>
          val req = request[TSPTaskDTO](Method.GET, "/").withEntity(json"""$json""")
          runWithEnv(
            check(
              app.run(req),
              Status.Ok,
              expectedBody
            )
          )
      }
    }

  }

}

object TSPSpec extends DefaultRuntime {

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
