package clover.tsp.front.http

import clover.tsp.front._
import clover.tsp.front.http.Service.TodoItemWithUri
import clover.tsp.front.repository.Repository
import clover.tsp.front.repository.Repository.InMemoryRepository
import io.circe.generic.auto._
import org.http4s.implicits._
import org.http4s.{ Status, _ }
import zio._
import zio.interop.catz._

class SimpleSpec extends HTTPSpec {
  import SimpleSpec._
  import SimpleSpec.todoService._

  val app = todoService.service.orNotFound

  describe("Service") {

    it("should create new todo items") {
      val req = request(Method.POST, "/").withEntity(TodoItemPostForm("Test"))
      runWithEnv(check(app.run(req), Status.Created, Some(TodoItemWithUri(1L, "/1", "Test", false, None))))
    }

    it("should list all todo items") {
      val setupReq = request[TodoTask](Method.POST, "/").withEntity(TodoItemPostForm("Test"))
      val req      = request[TodoTask](Method.GET, "/")
      runWithEnv(
        check[TodoTask, List[TodoItemWithUri]](
          app.run(setupReq) *> app.run(setupReq) *> app.run(req),
          Status.Ok,
          Some(
            List(
              TodoItemWithUri(1L, "/1", "Test", false, None),
              TodoItemWithUri(2L, "/2", "Test", false, None)
            )
          )
        )
      )
    }

    it("should delete todo items by id") {
      val setupReq  = request[TodoTask](Method.POST, "/").withEntity(TodoItemPostForm("Test"))
      val deleteReq = (id: Long) => request[TodoTask](Method.DELETE, s"/$id")
      val req       = request[TodoTask](Method.GET, "/")
      runWithEnv(
        check[TodoTask, List[TodoItemWithUri]](
          app
            .run(setupReq)
            .flatMap(resp => resp.as[TodoItemWithUri].map(_.id))
            .flatMap(id => app.run(deleteReq(id))) *> app.run(req),
          Status.Ok,
          Some(Nil)
        )
      )
    }

    it("should delete all todo items") {
      val setupReq  = request[TodoTask](Method.POST, "/").withEntity(TodoItemPostForm("Test"))
      val deleteReq = request[TodoTask](Method.DELETE, "/")
      val req       = request[TodoTask](Method.GET, "/")
      runWithEnv(
        check[TodoTask, List[TodoItemWithUri]](
          app.run(setupReq) *> app.run(setupReq) *> app.run(deleteReq) *> app.run(req),
          Status.Ok,
          Some(Nil)
        )
      )
    }

    it("should update todo items") {
      val setupReq = request[TodoTask](Method.POST, "/").withEntity(TodoItemPostForm("Test"))
      val updateReq =
        (id: Long) => request[TodoTask](Method.PATCH, s"/$id").withEntity(TodoItemPatchForm(title = Some("Test1")))
      val req = request[TodoTask](Method.GET, "/")
      runWithEnv(
        check[TodoTask, List[TodoItemWithUri]](
          app
            .run(setupReq)
            .flatMap(resp => resp.as[TodoItemWithUri].map(_.id))
            .flatMap(id => app.run(updateReq(id))) *> app.run(req),
          Status.Ok,
          Some(
            List(
              TodoItemWithUri(1L, "/1", "Test1", false, None)
            )
          )
        )
      )
    }
  }
}

object SimpleSpec extends DefaultRuntime {

  val todoService: Service[Repository] = Service[Repository]("")

  val mkEnv: UIO[Repository] =
    for {
      store   <- Ref.make(Map[TodoId, TodoItem]())
      counter <- Ref.make(0L)
      repo    = InMemoryRepository(store, counter)
      env = new Repository {
        override val todoRepository: Repository.Service[Any]         = repo
        override val dbInfoRepository: Repository.SimpleService[Any] = null
      }
    } yield env

  def runWithEnv[E, A](task: ZIO[Repository, E, A]): A =
    unsafeRun[E, A](mkEnv.flatMap(env => task.provide(env)))
}
