package clover.tsp.front.repository.implementations

import clover.tsp.front.domain.{ TodoId, TodoItem, TodoItemPatchForm, TodoItemPostForm, TodoPayload }
import clover.tsp.front.repository.interfaces.Repository
import clover.tsp.front.repository.implementations.DoobieRepository.SQL
import doobie._
import doobie.implicits._
import zio.interop.catz._
import zio.{ Task, ZIO }
import cats.implicits._
import doobie.free.connection

trait DoobieRepository extends Repository {

  protected def xa: Transactor[Task]

  override val dbInfoRepository: Repository.SimpleService[Any] = null

  override val todoRepository: Repository.Service[Any] =
    new Repository.Service[Any] {

      override def getAll: ZIO[Any, Nothing, List[TodoItem]] =
        SQL.getAll
          .to[List]
          .transact(xa)
          .orDie

      override def getById(id: TodoId): ZIO[Any, Nothing, Option[TodoItem]] =
        SQL
          .get(id)
          .option
          .transact(xa)
          .orDie

      override def delete(id: TodoId): ZIO[Any, Nothing, Unit] =
        SQL
          .delete(id)
          .run
          .transact(xa)
          .unit
          .orDie

      override def deleteAll: ZIO[Any, Nothing, Unit] =
        SQL.deleteAll.run
          .transact(xa)
          .unit
          .orDie

      override def create(todoItemForm: TodoItemPostForm): ZIO[Any, Nothing, TodoItem] =
        SQL
          .create(todoItemForm.asTodoPayload)
          .withUniqueGeneratedKeys[Long]("ID")
          .map(id => todoItemForm.asTodoItem(TodoId(id)))
          .transact(xa)
          .orDie

      override def update(id: TodoId, todoItemForm: TodoItemPatchForm): ZIO[Any, Nothing, Option[TodoItem]] =
        (for {
          oldItem <- SQL.get(id).option
          newItem = oldItem.map(_.update(todoItemForm))
          _       <- newItem.fold(connection.unit)(item => SQL.update(item).run.void)
        } yield newItem)
          .transact(xa)
          .orDie

    }
}

object DoobieRepository {

  object SQL {

    def create(todo: TodoPayload): Update0 = sql"""
      INSERT INTO TODOS (TITLE, COMPLETED, ORDERING)
      VALUES (${todo.title}, ${todo.completed}, ${todo.order})
      """.update

    def get(id: TodoId): Query0[TodoItem] = sql"""
      SELECT * FROM TODOS WHERE ID = ${id.value}
      """.query[TodoItem]

    val getAll: Query0[TodoItem] = sql"""
      SELECT * FROM TODOS
      """.query[TodoItem]

    def delete(id: TodoId): Update0 = sql"""
      DELETE from TODOS WHERE ID = ${id.value}
      """.update

    val deleteAll: Update0 = sql"""
      DELETE from TODOS
      """.update

    def update(todoItem: TodoItem): Update0 = sql"""
        UPDATE TODOS SET
        TITLE = ${todoItem.item.title},
        COMPLETED = ${todoItem.item.completed},
        ORDERING = ${todoItem.item.order}
        WHERE ID = ${todoItem.id.value}
      """.update
  }

}
