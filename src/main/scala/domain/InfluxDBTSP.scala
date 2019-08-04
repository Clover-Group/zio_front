package clover.tsp.front.domain

import io.circe.generic.JsonCodec

@JsonCodec
final case class InfluxDBSink(
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
final case class InfluxDBSource(
  url: String,
  dbName: String,
  username: String,
  password: String,
  query: String,
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
