package clover.tsp.front.domain

final case class PgRowSchema(
  toTsField: String,
  fromTsField: String,
  contextField: String,
  sourceIdField: String,
  patternIdField: String,
  forwardedFields: List[String],
  processingTsField: String,
  appIdFieldVal: List[String],
  values: List[String]
)

final case class PgSink(
  jdbcUrl: String,
  password: String,
  userName: String,
  tableName: String,
  driverName: String,
  parallelism: Int,
  batchInterval: Int,
  rowSchema: PgRowSchema
)

final case class PgSource(
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

final case class PgTSPTask(
  pgUniqueKey: String,
  sink: PgSink,
  uuid: String,
  patterns: List[Rule],
  source: PgSource
) extends TSPTask
