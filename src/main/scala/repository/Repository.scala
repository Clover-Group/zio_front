package clover.tsp.front.repository

import clover.tsp.front._
import zio._

trait Repository extends Serializable {

  val todoRepository: Repository.Service[Any]
  val dbInfoRepository: Repository.SimpleService[Any]

}

object Repository extends Serializable {

  trait Service[R] extends Serializable {

    def getAll: ZIO[R, Nothing, List[TodoItem]]

    def getById(id: TodoId): ZIO[R, Nothing, Option[TodoItem]]

    def delete(id: TodoId): ZIO[R, Nothing, Unit]

    def deleteAll: ZIO[R, Nothing, Unit]

    def create(todoItemForm: TodoItemPostForm): ZIO[R, Nothing, TodoItem]

    def update(id: TodoId, todoItemForm: TodoItemPatchForm): ZIO[R, Nothing, Option[TodoItem]]

  }

  trait SimpleService[R] extends Serializable {
    def get(task: TSPTask): ZIO[R, Nothing, DBItem]
  }

  final case class DBInfoRepository(ref: Ref[DBItem], counter: Ref[Long]) extends SimpleService[Any] {
    override def get(task: TSPTask): ZIO[Any, Nothing, DBItem] =
      ref.get
  }

  final case class InMemoryRepository(ref: Ref[Map[TodoId, TodoItem]], counter: Ref[Long]) extends Service[Any] {

    override def getAll: ZIO[Any, Nothing, List[TodoItem]] =
      ref.get.map(_.values.toList)

    override def getById(id: TodoId): ZIO[Any, Nothing, Option[TodoItem]] =
      ref.get.map(_.get(id))

    override def delete(id: TodoId): ZIO[Any, Nothing, Unit] =
      ref.update(store => store - id).unit

    override def deleteAll: ZIO[Any, Nothing, Unit] =
      ref.update(_.empty).unit

    override def create(todoItemForm: TodoItemPostForm): ZIO[Any, Nothing, TodoItem] =
      for {
        newId <- counter.update(_ + 1) map TodoId
        todo  = todoItemForm.asTodoItem(newId)
        _     <- ref.update(store => store + (newId -> todo))
      } yield todo

    override def update(id: TodoId, todoItemForm: TodoItemPatchForm): ZIO[Any, Nothing, Option[TodoItem]] =
      for {
        oldValue <- getById(id)
        result <- oldValue.fold[UIO[Option[TodoItem]]](ZIO.succeed(None)) { x =>
                   val newValue = x.update(todoItemForm)
                   ref.update(store => store + (newValue.id -> newValue)) *> ZIO.succeed(Some(newValue))
                 }
      } yield result

  }

}
