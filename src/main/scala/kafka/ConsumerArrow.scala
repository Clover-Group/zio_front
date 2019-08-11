package arrowConsumer

import zio.{ DefaultRuntime }

import org.apache.kafka.common.serialization.Serdes
import zio.kafka.client.KafkaTestUtils.{ pollNtimes }
import zio.kafka.client.{ Consumer, Subscription }
import KafkaPkg._
import kafkaConsumer.KafkaConsumer.{ settings }

import ArrowPkg._

sealed abstract class KafkaArrowConsumer extends DefaultRuntime {
  def run(cfg: SlaveConfig): Unit
}

object KafkaArrowConsumer extends KafkaArrowConsumer {

  type BArr = Array[Byte]

  def run(cfg: SlaveConfig): Unit = {
    val subscription = Subscription.Topics(Set(cfg.topic))
    val cons         = Consumer.make[String, BArr](settings(cfg))(Serdes.String, Serdes.ByteArray)

    val out =
      cons.use { r =>
        for {
          _         <- r.subscribe(subscription)
          batch     <- pollNtimes(5, r)
          _         <- r.unsubscribe
          arr       = batch.map(_.value)
          reader    = deserialize(arr)
          schema    = reader.map(r => r.getVectorSchemaRoot.getSchema)
          empty     = reader.map(r => r.loadNextBatch)
          bytesRead = reader.map(r => r.bytesRead)
          rowCount  = reader.map(r => r.getVectorSchemaRoot.getRowCount)
          _         = println(schema)
        } yield empty
      }

    println(out)

  }
}
