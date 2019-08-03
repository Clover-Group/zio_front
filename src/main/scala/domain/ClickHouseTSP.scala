package clover.tsp.front.domain

import io.circe.generic.JsonCodec

@JsonCodec
final case class CHRowSchema(
  toTsField: String,
  fromTsField: String,
  contextField: String,
  sourceIdField: String,
  patternIdField: String,
  forwardedFields: List[String],
  processingTsField: String,
  appIdFieldVal: List[String]
)

@JsonCodec
final case class CHSink(
  jdbcUrl: String,
  password: String,
  userName: String,
  tableName: String,
  driverName: String,
  parallelism: Int,
  batchInterval: Int,
  rowSchema: CHRowSchema
)

@JsonCodec
final case class CHSource(
  query: String,
  jdbcUrl: String,
  password: String,
  sourceId: Int,
  userName: String,
  driverName: String,
  parallelism: Int,
  dateTimeField: String,
  eventsMaxGapMs: Int,
  partitionFields: List[String],
  defaultEventsGapMs: Int,
  numParallelSources: Int,
  patternsParallelism: Int
)

@JsonCodec
final case class CHTSPTask(
  sink: CHSink,
  uuid: String,
  patterns: List[Rule],
  source: CHSource
) extends TSPTask
