package clover.tsp.front.domain

import io.circe.generic.JsonCodec

final case class DBItem(data: String)

@JsonCodec
final case class RowSchema(
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
final case class Rule(
 id: String,
 payload: Map[String, String],
 sourceCode: String,
 forwardedFields: List[String]
)

trait Sink{
  val abstractParallelism: Int
  val abstractBatchInterval: Int
  val abstractRowSchema: RowSchema
}

trait Source{
  val abstractSourceId: Int
  val abstractParallelism: Int
  val abstractDateTimeField: String
  val abstractEventsMaxGapMs: Int
  val abstractPartitionFields: List[String]
  val abstractDefaultEventsGapMs: Int
  val abstractNumParallelSources: Int
  val abstractPatternsParallelism: Int
}
