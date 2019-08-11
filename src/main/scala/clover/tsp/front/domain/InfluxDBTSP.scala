package clover.tsp.front.domain

final case class InfluxDBSink(
  jdbcUrl: String,
  password: String,
  userName: String,
  tableName: String,
  driverName: String,
  parallelism: Int,
  batchInterval: Int,
  rowSchema: RowSchema
)

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
)

final case class InfluxDBTSPTask(

  sink: InfluxDBSink,
  uuid: String,
  patterns: List[Rule],
  source: InfluxDBSource

 )

