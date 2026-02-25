//> using target.platform jvm
//> using file "shared.scala"
//> using dep "com.softwaremill.sttp.tapir::tapir-vertx-server:1.13.8"
import migke.app.Data
import scala.concurrent.Future

@main def run = {
  val endpoint = Data.index
    .serverLogic((Unit) => Future.successful(Right("hello world")))
  Server().routes(endpoint).listen(8080)
}

case class Server(
    routeGroup: Seq[sttp.tapir.server.serverendpoint[
      sttp.tapir.server.vertx.streams.vertxstreams &
        sttp.capabilities.websockets,
      scala.concurrent.future
    ]] = Seq()
) {
  def listen(port: Int) = {
    import sttp.tapir.*
    import sttp.tapir.server.vertx.VertxFutureServerInterpreter
    import sttp.tapir.server.vertx.VertxFutureServerInterpreter.*
    import io.vertx.core.Vertx
    import io.vertx.ext.web.*
    import scala.concurrent.{Await, Future}
    import scala.concurrent.duration.*
    val vertx = Vertx.vertx()
    val server = vertx.createHttpServer()
    val router = Router.router(vertx)
    val interpreter = VertxFutureServerInterpreter()
    def attaches = routes.map(interpreter.route(_))
    attaches.foreach(_(router))
    Await.result(
      server.requestHandler(router).listen(port).asScala,
      Duration.Inf
    )
  }
  def routes(
      routes: sttp.tapir.server.serverendpoint[
        sttp.tapir.server.vertx.streams.vertxstreams &
          sttp.capabilities.websockets,
        scala.concurrent.future
      ]*
  ) = Server(routes ++ this.routeGroup)
}
