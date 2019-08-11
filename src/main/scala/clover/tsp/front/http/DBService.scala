package clover.tsp.front.http

import clover.tsp.front.domain.{ CHTSPTask, KafkaTSPTask, PgTSPTask, TSPTask }
import clover.tsp.front.repository.implementations.{ PgInsert, PostgresRepository }
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.{ EntityDecoder, EntityEncoder, HttpRoutes }
import com.typesafe.scalalogging.Logger
import zio.{ RIO, ZIO }
import zio.interop.catz._
import cats.syntax.functor._
import clover.tsp.front.repository.implementations.CHSinkRepository
import io.circe.{ Decoder, Encoder }
import io.circe.generic.auto._
import io.circe.syntax._
import clover.tsp.front.repository.interfaces.Repository

final case class DBService[R <: Repository](rootUri: String, pgRepository: PostgresRepository) {
  type TSPTaskDTO[A] = RIO[R, A]

  implicit def tspTaskJsonDecoder[A](implicit decoder: Decoder[A]): EntityDecoder[TSPTaskDTO, A] = jsonOf[TSPTaskDTO, A]
  implicit def tspTaskJsonEncoder[A](implicit decoder: Encoder[A]): EntityEncoder[TSPTaskDTO, A] =
    jsonEncoderOf[TSPTaskDTO, A]

  implicit val encodeTask: Encoder[TSPTask] = Encoder.instance {
    case kafkaTask @ KafkaTSPTask(_, _, _, _) => kafkaTask.asJson
    case chTask @ CHTSPTask(_, _, _, _)       => chTask.asJson
    case pgTask @ PgTSPTask(_, _, _, _, _)    => pgTask.asJson
  }

  implicit val decodeEvent: Decoder[TSPTask] =
    List[Decoder[TSPTask]](
      Decoder[PgTSPTask].widen,
      Decoder[KafkaTSPTask].widen,
      Decoder[CHTSPTask].widen
    ).reduceLeft(_ or _)

  val dsl: Http4sDsl[TSPTaskDTO] = Http4sDsl[TSPTaskDTO]
  import dsl._

  val logger = Logger("DBService")

  def service: HttpRoutes[TSPTaskDTO] =
    HttpRoutes.of[TSPTaskDTO] {

      case req @ POST -> Root =>
        logger.info("Root method called")
        for {
          task <- req.as[TSPTask]
          _ = task match {
            case KafkaTSPTask(_, _, _, _) =>
              // TODO call instance of kafka service
              logger.info("Kafka JSON received")
            case chJson @ CHTSPTask(_, _, _, _) =>
              logger.info("ClickHouse JSON received")
              val chSinkRepository = CHSinkRepository(chJson.sink)
              chSinkRepository.insertOne()
            case PgTSPTask(_, sink, _, _, _) =>
              logger.info("Postgres JSON received")
              val columns = List(sink.rowSchema.fromTsField, sink.rowSchema.toTsField)

              val insert = PgInsert(
                sink.tableName,
                columns,
                sink.rowSchema.values
              )
              pgRepository.write(insert)

          }
          res <- Ok(ZIO.effect(()))
        } yield res
    }
}
