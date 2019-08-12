package clover.tsp.front.repository.implementations

import KafkaPkg._
import arrowConsumer._
import kafkaConsumer.KafkaConsumer.{ settings }
import clover.tsp.front.domain.KafkaSource

case class KafkaSourceRepository(source: KafkaSource) {

  private val cfg = SlaveConfig(
    server = source.server,
    client = source.client,
    group = source.group,
    topic = source.topic
  )

  def select(): Unit = KafkaArrowConsumer.run(cfg)

}
