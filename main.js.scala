//> using target.platform scala-js
//> using file "shared.scala"
//> using dep "org.scala-js::scalajs-dom::2.8.1"
package migke.app.front
import org.scalajs.dom.*
@main def run = {
  val list = create[HTMLUListElement]("ul")
    .pipe(_.innerText = "No tasks")
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
    objs.foreach(c => this.pipe(_.appendChild(c.element)))
  def appendTo(parentSelector: String) = {
    val res = element
    document.querySelector(parentSelector).appendChild(res)
    DOMObject(res)
  }
}
