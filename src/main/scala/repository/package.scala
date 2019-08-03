package clover.tsp.front

import clover.tsp.front.repository.Repository
import zio.ZIO

package object repository extends Repository.Service[Repository] {

  override def create(todoItemForm: TodoItemPostForm): ZIO[Repository, Nothing, TodoItem] =
    ZIO.accessM(_.todoRepository.create(todoItemForm))

  override def getById(id: TodoId): ZIO[Repository, Nothing, Option[TodoItem]] =
    ZIO.accessM(_.todoRepository.getById(id))

  override def getAll: ZIO[Repository, Nothing, List[TodoItem]] = ZIO.accessM(_.todoRepository.getAll)

  override def delete(id: TodoId): ZIO[Repository, Nothing, Unit] = ZIO.accessM(_.todoRepository.delete(id))

  override def deleteAll: ZIO[Repository, Nothing, Unit] = ZIO.accessM(_.todoRepository.deleteAll)

  override def update(id: TodoId, todoItemForm: TodoItemPatchForm): ZIO[Repository, Nothing, Option[TodoItem]] =
    ZIO.accessM(_.todoRepository.update(id, todoItemForm))

}

package object simpleRepository extends Repository.SimpleService[Repository] {
  override def get(task: TSPTask): ZIO[Repository, Nothing, DBItem] =
    ZIO.accessM(_.dbInfoRepository.get(task))
}
