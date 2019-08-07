package kafkaIntegration

import zio.{ DefaultRuntime }

import zio.kafka.client.KafkaTestUtils.{ pollNtimes }
import zio.kafka.client.{ Consumer, Subscription }


final case class SlaveConfig(
  server: String,
  client: String,
  group: String,
  topic: String
)

sealed abstract class KafkaArrowConsumer extends DefaultRuntime {
  def run(cfg: SlaveConfig): Unit
}

object kafkaIntegration extends KafkaArrowConsumer {

  def run(cfg: SlaveConfig): Unit = {
    val subscription = Subscription.Topics(Set(slvCfg.topic))
    val cons         = Consumer.make[String, BArr](settings(slvCfg))(Serdes.String, Serdes.ByteArray)

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
        } yield (empty, rowCount, schema)
      }

      println (out._1)
      println (out._2)
      println (out._3)

  }

}
