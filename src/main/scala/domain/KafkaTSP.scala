package clover.tsp.front.domain

import io.circe.generic.JsonCodec

@JsonCodec
final case class KafkaSink(
  driverName: String,
  topic: String,
  parallelism: Int,
  batchInterval: Int,
  rowSchema: RowSchema
)

@JsonCodec
final case class KafkaSource(
  sourceId: Int,
  driverName: String,
  columns: List[String],
  topic: String,
  parallelism: Int,
  datetimeField: String,
  eventsMaxGapMs: Int,
  partitionFields: List[String],
  defaultEventsGapMs: Int,
  numParallelSources: Int,
  patternsParallelism: Int
)

@JsonCodec
final case class KafkaTSPTask(
  sink: KafkaSink,
  uuid: String,
  patterns: List[Rule],
  source: KafkaSource
) extends TSPTask
