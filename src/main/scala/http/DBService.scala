package clover.tsp.front.http

import clover.tsp.front.{ simpleRepository, TSPTask }
import clover.tsp.front.repository.Repository
import io.circe.generic.auto._
import io.circe.{ Decoder, Encoder }
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.{ EntityDecoder, EntityEncoder, HttpRoutes }
import com.typesafe.scalalogging.Logger
import zio.TaskR
import zio.interop.catz._

final case class DBService[R <: Repository](rootUri: String) {
  type TSPTaskDTO[A] = TaskR[R, A]

  implicit def tspTaskJsonDecoder[A](implicit decoder: Decoder[A]): EntityDecoder[TSPTaskDTO, A] = jsonOf[TSPTaskDTO, A]
  implicit def tspTaskJsonEncoder[A](implicit decoder: Encoder[A]): EntityEncoder[TSPTaskDTO, A] =
    jsonEncoderOf[TSPTaskDTO, A]

  val dsl: Http4sDsl[TSPTaskDTO] = Http4sDsl[TSPTaskDTO]
  import dsl._

  val log = Logger("DBService")

  def service: HttpRoutes[TSPTaskDTO] =
    HttpRoutes.of[TSPTaskDTO] {

      case req @ POST -> Root =>
        log.debug("Root method called")
        for {
          task       <- req.as[TSPTask]
          dbInfoItem <- simpleRepository.get(task)
          res        <- Ok(dbInfoItem)
        } yield res
    }
}
