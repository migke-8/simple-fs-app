//> using target.platform scala-js
//> using file "shared.scala"
//> using dep "org.scala-js::scalajs-dom::2.8.1"
//> using dep "com.softwaremill.sttp.tapir::tapir-sttp-client4:1.13.9"
package migke.app.front
import org.scalajs.dom.*
import sttp.tapir.*
import sttp.tapir.client.sttp4.*
import sttp.client4.*
import migke.app.Data.all
import sttp.client4.httpclient.HttpClientSyncBackend
@main def run = {
  val list = create[HTMLUListElement]("ul")
    .pipe(_.id = "list")
    .pipe(el =>
        fromDB.map(_ match {
          case Right(v) => v.map(translateToDOM(_))
          case _ => {}
        })
    )
    .appendTo("body")
  val input = create[HTMLInputElement]("input")
    .pipe(_.`type` = "text")
    .appendTo("body")
  val btn = create[HTMLButtonElement]("button")
    .pipe(_.innerText = "add")
    .pipe(
      _.addEventListener(
        "click",
        { _ =>
          list.pipe(_.innerText = "")
          create[HTMLDataListElement]("li")
            .pipe(_.innerText = input.element.value)
            .appendTo("ul")
        }
      )
    )
    .appendTo("body")
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
def fromDB = SttpClientInterpreter()
  .toRequest(all, Some(uri"http://localhost:8080"))(())
  .send(HttpClientSyncBackend())
  .body match {
  case DecodeResult.Value(v)    => v
  case _ => println("error")
}
def translateToDOM(item: migke.app.Item) =
  create[HTMLUListElement]("li").children(create("div").pipe(_.innerText = item.task))
