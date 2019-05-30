package clover.tsp.front.http

import clover.tsp.front._
import clover.tsp.front.http.Service.TodoItemWithUri
import clover.tsp.front.repository.Repository
import clover.tsp.front.repository.Repository.InMemoryRepository
import io.circe.generic.auto._
import org.http4s.implicits._
import org.http4s.{Status, _}

import fs2.Stream
import java.nio.file.Paths

import cats.effect._
import io.circe._
import io.circe.literal._

import org.http4s.circe._
import org.http4s.dsl.io._

import scalaz.zio.{ZIO, UIO, Ref, DefaultRuntime}
import scalaz.zio.interop.catz._


class JsonSpec extends HTTPSpec {
  import JsonSpec._
  import JsonSpec.todoService._

  val app = todoService.service.orNotFound

  describe("Simple Service") {

    it("should create new todo items") {
      val req     = request(Method.POST, "/").withEntity(TodoItemPostForm("Test"))
      runWithEnv(
        check(
          app.run(req),
          Status.Created,
          Some(TodoItemWithUri(1L, "/1", "Test", false, None))))
    }

    it("work with json") {
      
    def hello(name: String): Json = json"""{"hello": $name}"""

    val greet  = hello("world")
    val stream  = Stream.eval(IO{greet})
    
    //val req     = request(Method.POST, "/").withEntity(TodoItemPostForm("Test"))
    val req     = request(Method.POST, "/").withBody(hello("world")).flatMap ( r => 
      runWithEnv(
        check(
          app.run(r),
          Status.Created,
          Some(TodoItemWithUri(1L, "/1", "Test", false, None))))
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
