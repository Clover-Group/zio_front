package clover.tsp.front.http

import clover.tsp.front.domain.{ CHTSPTask, DBItem, KafkaTSPTask, TSPTask }
import clover.tsp.front.repository.Repository
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.{ EntityDecoder, EntityEncoder, HttpRoutes }
import com.typesafe.scalalogging.Logger
import zio.{ Ref }
import zio.TaskR
import zio.interop.catz._
import clover.tsp.front.repository.DBInfoRepository
import cats.syntax.functor._
import io.circe.{ Decoder, Encoder }, io.circe.generic.auto._
import io.circe.syntax._

final case class DBService[R <: Repository](rootUri: String) {
  type TSPTaskDTO[A] = TaskR[R, A]

  implicit def tspTaskJsonDecoder[A](implicit decoder: Decoder[A]): EntityDecoder[TSPTaskDTO, A] = jsonOf[TSPTaskDTO, A]
  implicit def tspTaskJsonEncoder[A](implicit decoder: Encoder[A]): EntityEncoder[TSPTaskDTO, A] =
    jsonEncoderOf[TSPTaskDTO, A]

  implicit val encodeTask: Encoder[TSPTask] = Encoder.instance {
    case kafkaTask @ KafkaTSPTask(_, _, _, _) => kafkaTask.asJson
    case chTask @ CHTSPTask(_, _, _, _)       => chTask.asJson
  }

  implicit val decodeEvent: Decoder[TSPTask] =
    List[Decoder[TSPTask]](
      Decoder[KafkaTSPTask].widen,
      Decoder[CHTSPTask].widen
    ).reduceLeft(_ or _)

  val dsl: Http4sDsl[TSPTaskDTO] = Http4sDsl[TSPTaskDTO]
  import dsl._

  val log = Logger("DBService")

  def service: HttpRoutes[TSPTaskDTO] =
    HttpRoutes.of[TSPTaskDTO] {

      case req @ POST -> Root =>
        log.debug("Root method called")
        log.debug(s"req: $req")
        for {
          store   <- Ref.make(DBItem("some data"))
          counter <- Ref.make(0L)
          repo    = DBInfoRepository(store, counter)
          task    <- req.as[TSPTask]
          dbInfoItem <- task match {
                         case kafkaJson @ KafkaTSPTask(_, _, _, _) => {
                           // TODO call instance of kafka service
                           print("!!!!!!!!! kafka !!!!!!!!!!!!")
                           repo.get(kafkaJson);
                         }
                         case chJson @ CHTSPTask(_, _, _, _) => {
                           // TODO call instance of ClickHouse service
                           print("!!!!!!!!! ClickHouse !!!!!!!!!!!!")
                           repo.get(chJson);
                         }
                       }
          res <- Ok(dbInfoItem)
        } yield res
    }
}
