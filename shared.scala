//> using scala 3.3.7
//> using target.platform "scala-js" "jvm"
//> using jsVersion "1.15.0"
//> using dep "com.softwaremill.sttp.tapir::tapir-core:1.13.9"
//> using dep "com.softwaremill.sttp.tapir::tapir-json-upickle:1.13.9"
package migke.app
import sttp.tapir.*
import scala.compiletime.ops.string
object Data {
  def index = endpoint.get.in("")
}
