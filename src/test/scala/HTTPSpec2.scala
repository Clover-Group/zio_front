package clover.tsp.front

import cats.effect.Sync
import cats.implicits._
import org.http4s.{ EntityDecoder, Method, Request, Response, Status, Uri }
import org.scalatest.Assertion
import org.specs2._

trait HTTPSpec2 extends Specification {

  protected def request[F[_]](method: Method, uri: String): Request[F] =
    Request(method = method, uri = Uri.fromString(uri).toOption.get)

  protected def check[F[_], A](
    actual: F[Response[F]],
    expectedStatus: Status,
    expectedBody: Option[A]
  )(
    implicit
    F: Sync[F],
    ev: EntityDecoder[F, A]
  ): F[Unit] =
    for {
      actual <- actual
      _ <- expectedBody.fold(actual.body.compile.toVector.map(s => s.isEmpty))(
            _ =>
              actual
                .as[A]
                .map(x => {
                  val result = x === expectedBody
                  result
                })
          )
      _ <- F.delay(actual.status must_== expectedStatus)
    } yield ()
}
