//> using scala 3.3.7
//> using platform "scala-js" "jvm"
//> using dep "com.softwaremill.sttp.tapir::tapir-core:1.13.13"
//> using dep "com.softwaremill.sttp.tapir::tapir-json-upickle:1.13.13"
package migke.app
import sttp.tapir.*
// import scala.compiletime.ops.string
// import sttp.tapir.generic.auto.*
// case class Item(task: String, done: Boolean, id: Int)
// object Item {
//   given upickle.default.ReadWriter[Item] = upickle.default.macroRW
// }
def point = endpoint.get.in("").out(stringBody)
