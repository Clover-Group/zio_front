package clover.tsp.front

import io.circe.generic.JsonCodec

final case class TodoId(value: Long) extends AnyVal

final case class TodoPayload(
  title: String,
  completed: Boolean,
  order: Option[Int]
)

final case class TodoItem(
  id: TodoId,
  item: TodoPayload
) {

  def update(form: TodoItemPatchForm): TodoItem =
    this.copy(
      id = this.id,
      item = item.copy(
        title = form.title.getOrElse(item.title),
        completed = form.completed.getOrElse(item.completed),
        order = form.order.orElse(item.order)
      )
    )
}

final case class TodoItemPostForm(
  title: String,
  order: Option[Int] = None
) {

  def asTodoItem(id: TodoId): TodoItem =
    TodoItem(id, this.asTodoPayload)

  def asTodoPayload: TodoPayload =
    TodoPayload(title, false, order)

}

final case class TodoItemPatchForm(
  title: Option[String] = None,
  completed: Option[Boolean] = None,
  order: Option[Int] = None
)

final case class DBInfoForm(
  source: String,
  sink: String,
  dbType: String,
  query: String
)

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
final case class Sink(
  jdbcUrl: String,
  password: String,
  userName: String,
  tableName: String,
  driverName: String,
  parallelism: Int,
  batchInterval: Int,
  rowSchema: RowSchema
)

@JsonCodec
final case class Source(
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
final case class Rule(
  id: String,
  payload: Map[String, String],
  sourceCode: String,
  forwardedFields: List[String]
)

@JsonCodec
final case class TSPTask(
  sink: Sink,
  uuid: String,
  patterns: List[Rule],
  source: Source
)

final case class DBItem(data: String)
