package clover.tsp.front.repository.implementations

import com.dimafeng.testcontainers.PostgreSQLContainer
import doobie.implicits._
import doobie.util.fragment.Fragment
import doobie.util.transactor.Transactor
import zio.Task

case class PgInsert(tableName: String, columns: List[String], values: List[String])
case class PgInsertResult(from: String, to: String)

case class PostgresRepository(tnx: Transactor[Task]) {
  import zio.interop.catz._

  def createTable(table: Fragment): Task[Unit] =
    table.update.run
      .transact(tnx)
      .foldM(err => {
        print(err)
        Task.fail(err)
      }, _ => Task.succeed(()))

  def write(insert: PgInsert): Task[Unit] = {
    sql"""INSERT INTO ${insert.tableName} (${insert.columns.mkString(",")}) VALUES (${insert.values.mkString(",")})""".update.run
      .transact(tnx)
      .foldM(err => Task.fail(err), _ => Task.succeed(insert))
  }

  def getByQuery(sql: Fragment): Task[List[String]] = {
      sql
      .query[String]
      .to[List]
      .transact(tnx)
      .foldM(err => Task.fail(err), list => Task.succeed(list))
  }

}

object PostgresRepository {
  import zio.interop.catz._

  def apply(tnx: Transactor[Task]): PostgresRepository = new PostgresRepository(tnx)

  def getTransactor(container: PostgreSQLContainer): Transactor[Task] = Transactor.fromDriverManager[Task](
    container.driverClassName,
    container.jdbcUrl,
    container.username,
    container.password
  )
}
