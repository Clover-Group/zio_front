package clover.tsp.front.domain

import io.circe.generic.JsonCodec

@JsonCodec
final case class CHSink(
  jdbcUrl: String,
  password: String,
  userName: String,
  tableName: String,
  driverName: String,
  parallelism: Int,
  batchInterval: Int,
  rowSchema: RowSchema
) extends Sink {
  override val abstractParallelism: Int     = parallelism
  override val abstractBatchInterval: Int   = batchInterval
  override val abstractRowSchema: RowSchema = rowSchema
}

@JsonCodec
final case class CHSource(
  query: String,
  jdbcUrl: String,
  password: String,
  userName: String,
  driverName: String,
  sourceId: Int,
  parallelism: Int,
  dateTimeField: String,
  eventsMaxGapMs: Int,
  partitionFields: List[String],
  defaultEventsGapMs: Int,
  numParallelSources: Int,
  patternsParallelism: Int
) extends Source {
  override val abstractSourceId: Int                 = sourceId
  override val abstractParallelism: Int              = parallelism
  override val abstractDateTimeField: String         = dateTimeField
  override val abstractEventsMaxGapMs: Int           = eventsMaxGapMs
  override val abstractPartitionFields: List[String] = partitionFields
  override val abstractDefaultEventsGapMs: Int       = defaultEventsGapMs
  override val abstractNumParallelSources: Int       = numParallelSources
  override val abstractPatternsParallelism: Int      = patternsParallelism
}

@JsonCodec
final case class CHTSPTask(
  sink: CHSink,
  uuid: String,
  patterns: List[Rule],
  source: CHSource
)
