package io.taskmonk.entities

import java.util.Date

import play.api.libs.json.Json


case class FieldSummary(fieldId: Option[String], fieldName: Option[String],
                        field_type: Option[String],
                        computation_level_owner: Option[Seq[String]],
                        display_sequence: Option[Int], client_operations: Option[Boolean], mandatory: Option[Boolean])
object FieldSummary {
  implicit  val reads = Json.reads[FieldSummary]
  implicit  val writes = Json.writes[FieldSummary]
}

case class Field(id: String,
                 project_id: String,
                 field_name: String,
                 field_type: Option[String],
                 possible_values: Option[Seq[String]],
                 visible_name: Option[String],
                 custom_field: Option[Boolean],
                 computation_level_owner: Option[Seq[String]],
                 display_sequence: Option[Int],
                 is_unique: Boolean,
                 client_operations: Option[Boolean],
                 mandatory: Option[Boolean]) {
  def toSummary(): FieldSummary = {
    FieldSummary(fieldId = Some(id), fieldName = Some(field_name), field_type = field_type,
      computation_level_owner = computation_level_owner,
      display_sequence = display_sequence, client_operations = client_operations, mandatory = mandatory )
  }
}

case class FieldValue(fieldSummary: FieldSummary, fieldValue: String) {
  def findFieldFrom(fields: Seq[Field]): Option[Field] = {
    fields.find { x =>
      if (fieldSummary.fieldId.isDefined && x.id.equalsIgnoreCase(fieldSummary.fieldId.get)) {
        true
      } else if (fieldSummary.fieldName.isDefined && x.field_name.equalsIgnoreCase(fieldSummary.fieldName.get)) {
        true
      } else {
        false
      }
    }
  }
}

object FieldValue {
  implicit val reads = Json.reads[FieldValue]
  implicit val writes = Json.writes[FieldValue]
}

case class FieldsRow(fieldValues: Seq[FieldValue])
object FieldsRow {
  implicit  val reads = Json.reads[FieldsRow]
  implicit  val writes = Json.writes[FieldsRow]
}


case class TaskInput(inputFields: Seq[FieldValue])
object TaskInput {
  implicit  val reads = Json.reads[TaskInput]
  implicit  val writes = Json.writes[TaskInput]
}
case class TaskOutput(outputValues: Map[String, FieldsRow])

object TaskOutput {
  implicit  val reads = Json.reads[TaskOutput]
  implicit  val writes = Json.writes[TaskOutput]
}

case class ErrorLevels(errorLevels: Map[String, Seq[String]])

object ErrorLevels {
  implicit val reads = Json.reads[ErrorLevels]
  implicit val writes = Json.writes[ErrorLevels]
}



case class TaskValues(taskId: String, batchId: String, inputValues: Option[TaskInput], outputValues: Option[TaskOutput], error: Option[ErrorLevels])

object TaskValues {

  implicit val reads = Json.reads[TaskValues]
  implicit val writes = Json.writes[TaskValues]

}


object Task {

  implicit val reads = Json.reads[Task]
  implicit val writes = Json.writes[Task]
}
case class Task(id: String,
                project_id: String,
                batch_id: String,
                next_level: Short,
                status: Short,
                input: Option[TaskInput] = None,
                output: Option[TaskOutput] = None,
                unique_field_value: Option[String] = None,
                active_user: Option[String] = None,
                error: Option[ErrorLevels] = None,
                lastModifiedTime: Option[Date] = Some(new Date())) {

  def this(id: String,
           project_id: String,
           batch_id: String,
           next_level: Short,
           status: Short) = this(id, project_id, batch_id, next_level, status, None, None, None, None, None, None)
  def getInputString(): Option[String] = {
    input.map(Json.toJson(_).toString())
  }

  def getOutputString(): Option[String] = {
    output.map(Json.toJson(_).toString())
  }
  def toTaskValues(): TaskValues = {
    TaskValues(id, batch_id, input, output, error)
  }

}

