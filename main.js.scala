//> using target.platform scala-js
//> using jsVersion "1.15.0"
//> using file "shared.scala"
//> using dep "org.scala-js::scalajs-dom::2.8.1"
//> using dep "com.softwaremill.sttp.tapir::tapir-sttp-client4_sjs1:1.13.13"
//> using dep "com.softwaremill.sttp.client4::core:4.0.20"
package migke.app.front
import org.scalajs.dom.*
import sttp.tapir.*
import sttp.tapir.client.sttp4.*
import sttp.client4.*
import migke.app.point
import sttp.client4.fetch.FetchBackend
import sttp.model.Method
import sttp.model.Uri
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
given ExecutionContext = ExecutionContext.global
@main def run() = {
  val h1 = create[HTMLHeadingElement]("h1")
    .pipe(_.style.color = "red")
    .appendTo("body")
  fromDB.foreach(res => h1.pipe(_.innerText = res.body))
}
def create[T <: Element](name: String) = DOMObject(
  document.createElement(name).asInstanceOf[T]
)
case class DOMObject[T <: Element](element: T) {
  def pipe(lamb: T => Unit) = {
    val clone = element.cloneNode(true).asInstanceOf[T]
    lamb.apply(clone)
    if (!clone.equals(element)) element.replaceWith(clone)
    DOMObject(clone)
  }
  def children(objs: DOMObject[?]*) =
    objs.foldLeft(this)((prev, cur) => prev.pipe(_.appendChild(cur.element)))
  def appendTo(parentSelector: String) = {
    val res = element
    document.querySelector(parentSelector).appendChild(res)
    DOMObject(res)
  }
}
def fromDB = SttpClientInterpreter().toRequest(point, Some(Uri("localhost:8080"))).apply(()).response(asStringAlways).send(FetchBackend())
