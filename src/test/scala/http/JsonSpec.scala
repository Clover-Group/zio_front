package clover.tsp.front.http

import clover.tsp.front._
import clover.tsp.front.http.Service.TodoItemWithUri
import clover.tsp.front.repository.Repository
import clover.tsp.front.repository.Repository.InMemoryRepository

import io.circe.literal._
import io.circe.generic.auto._

import org.http4s._
import org.http4s.implicits._
import org.http4s.dsl.Http4sDsl

<<<<<<< HEAD
import scalaz.zio.{ZIO, UIO, Ref, DefaultRuntime}
import scalaz.zio.interop.catz._
=======
import zio.{ DefaultRuntime, Ref, UIO, ZIO }
import zio.interop.catz._
>>>>>>> dev


class JsonSpec extends HTTPSpec {
  import JsonSpec._
  import JsonSpec.todoService._

  val app = todoService.service.orNotFound

  val dsl: Http4sDsl[TodoTask] = Http4sDsl[TodoTask]
  //import dsl._

  describe("Simple Service") {

    it("should create new todo items") {
      val req     = request(Method.POST, "/").withEntity(TodoItemPostForm("Test"))
      runWithEnv(
        check(
          app.run(req),
          Status.Created,
          Some(TodoItemWithUri(1L, "/1", "Test", false, None))))
    }

    it("should parse json") {
      
    val body = json"""
      {
        "id"        : 33        ,
        "url"       :"/testUrl" ,
        "title"     :"One"      ,
        "completed" : false     ,
        "order"     : "None"
      }"""

    val req = request[TodoTask](Method.POST, "/").withEntity(body)

    runWithEnv(
      check(
        app.run(req),
        Status.Ok,
        Some(Nil))
        //Some(TodoItemWithUri(1L, "/1", "Test", false, None))
    )
    }
  }
}

object JsonSpec extends DefaultRuntime {

  val todoService: Service[Repository] = Service[Repository]("")

  val mkEnv: UIO[Repository] =
    for {
      store    <- Ref.make(Map[TodoId, TodoItem]())
      counter  <- Ref.make(0L)
      repo      = InMemoryRepository(store, counter)
      env       = new Repository {
                    override val todoRepository: Repository.Service[Any] = repo
                  }
    } yield env

  def runWithEnv[E, A](task: ZIO[Repository, E, A]): A =
    unsafeRun[E, A](mkEnv.flatMap(env => task.provide(env)))
}
