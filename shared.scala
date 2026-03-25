//> using scala 3.3.7
//> using target.platform "scala-js" "jvm"
//> using jsVersion "1.15.0"
//> using dep "com.softwaremill.sttp.tapir::tapir-core:1.13.9"
//> using dep "com.softwaremill.sttp.tapir::tapir-json-upickle:1.13.9"
package migke.app
import sttp.tapir.*
import scala.compiletime.ops.string
import sttp.tapir.generic.auto.*
case class Item(task: String, done: Boolean, id: Int)
object Item {
  given upickle.default.ReadWriter[Item] = upickle.default.macroRW
}
object Data {
  def all = endpoint.get.in("api" / "items").out(json.upickle.jsonBody[Seq[Item]])
}
