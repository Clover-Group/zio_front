package clover.tsp.front.repository.implementations

import clover.tsp.front.domain.CHSink
import doobie.util.transactor.Transactor
import zio.Task

import doobie.implicits._
import zio.interop.catz._

final case class CHSinkRepository(sink: CHSink) {

  private val transactor: Transactor[Task] = Transactor.fromDriverManager[Task](
    sink.driverName,
    sink.jdbcUrl,
    sink.userName,
    sink.password
  )

  def insertOne(): Task[Any] =
    sql"""INSERT INTO ${sink.tableName} (${sink.rowSchema.fromTsField}, ${sink.rowSchema.toTsField})
    |VALUES (${sink.rowSchema.values(0)}, ${sink.rowSchema.values(1)})""".update.run
      .transact(transactor)

}
