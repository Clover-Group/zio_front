package clover.tsp.front.repository.implementations

import clover.tsp.front.domain.CHRowSchema
import doobie.util.transactor.Transactor
import doobie.implicits._
import zio.interop.catz._
import zio.Task

trait PostgresRepository extends Serializable {
  val postgresRepository: Service[Any]
}

trait Service[R] {
  def write(tableName: String, chRowSchema: CHRowSchema): Task[Any]
}

trait Live extends PostgresRepository {

  protected def tnx: Transactor[Task]

  override val postgresRepository: Service[Any] = (tableName: String, values: CHRowSchema) => {

    sql"""INSERT INTO $tableName (${values.fromTsField}, ${values.toTsField})
    |VALUES (${values.values(0)}, ${values.values(0)})""".update.run
      .transact(tnx)

  }

}
