package clover.tsp.front.repository

import clover.tsp.front.domain.{DBItem, TSPTask}
import clover.tsp.front.repository.Repository.SimpleService
import zio.{Ref, ZIO}

final case class DBInfoRepository(ref: Ref[DBItem], counter: Ref[Long]) extends SimpleService[Any] {
  override def get(task: TSPTask): ZIO[Any, Nothing, DBItem] = ref.get
}


