import org.apache.avro.{Protocol => AvroProtocol, _}

import higherkindness.skeuomorph.mu.Transform.transformAvro
import higherkindness.skeuomorph.mu.MuF
import higherkindness.skeuomorph.mu.codegen
import higherkindness.skeuomorph.avro.AvroF.fromAvro
import higherkindness.droste._
import higherkindness.droste.data._
import higherkindness.droste.data.Mu._
import cats.implicits._
import scala.meta._

object Main {
  val definition = """
{
  "namespace": "example.avro",
  "type": "record",
  "name": "User",
  "fields": [
    {
      "name": "name",
      "type": "string"
    },
    {
      "name": "next",
      "type": [
        "User",
        "null"
      ]
    }
  ]
}
"""

  def main(args: Array[String]): Unit = {

    val avroSchema: Schema = new Schema.Parser().parse(definition)

    val toMuSchema: Schema => Mu[MuF] =
      scheme.hylo(transformAvro[Mu[MuF]].algebra, fromAvro)

    val printSchemaAsScala: Mu[MuF] => Either[String, String] = codegen.schema(_).map(_.syntax)

    (toMuSchema >>> println) (avroSchema)
    println("=====")
    (toMuSchema >>> printSchemaAsScala >>> println) (avroSchema)
  }
}
