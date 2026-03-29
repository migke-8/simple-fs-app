//> using target.platform jvm
//> using file "shared.scala"
//> using dep com.softwaremill.sttp.tapir::tapir-pekko-http-server:1.13.13
//> using dep com.softwaremill.sttp.tapir::tapir-json-upickle:1.13.13
//> using dep org.apache.pekko::pekko-http:1.3.0
//> using dep org.apache.pekko::pekko-stream:1.4.0
//> using dep com.lihaoyi::upickle:4.4.3
//> using dep "ch.qos.logback:logback-classic:1.5.32"
package migke.app.back
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.http.scaladsl.Http
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.upickle._
import sttp.tapir.server.pekkohttp.PekkoHttpServerInterpreter
import upickle.default.{ReadWriter, macroRW}
import scala.concurrent.{ExecutionContext, Future}
import scala.io.StdIn
import org.apache.pekko.actor.Actor
import migke.app.point
import sttp.tapir.server.interceptor.cors.CORSConfig
import sttp.model.Method
import sttp.tapir.server.interceptor.cors.CORSInterceptor
import sttp.tapir.server.pekkohttp.PekkoHttpServerOptions

given ExecutionContext = ExecutionContext.global
given system: ActorSystem = ActorSystem("pekko")

@main def run = {
  val customCorsConfig = CORSConfig.default.copy(
    allowedOrigin = CORSConfig.AllowedOrigin.Matching(
      Set("http://localhost:3000")
    )
  )

  val corsInterceptor = CORSInterceptor.customOrThrow[Future](customCorsConfig)

  val serverOptions = PekkoHttpServerOptions.customiseInterceptors
    .corsInterceptor(corsInterceptor)
    .options
  def route = PekkoHttpServerInterpreter(serverOptions).toRoute(
    point.serverLogic((Unit) => Future(Right("hello tapir")))
  )

  val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)

  StdIn.readLine()
  bindingFuture.flatMap(_.unbind()).onComplete(_ => system.terminate())
}

// @main def run = {
//   val endpoint = Data.all
//     .serverLogic((Unit) => Future.successful(Right()))
//   Server().routes(endpoint).listen(8080)
// }
//
// case class Server(
//     routeGroup: Seq[sttp.tapir.server.serverendpoint[
//       sttp.tapir.server.vertx.streams.vertxstreams &
//         sttp.capabilities.websockets,
//       scala.concurrent.future
//     ]] = Seq()
// ) {
//   def listen(port: Int) = {
//     import sttp.tapir.*
//     import sttp.tapir.server.vertx.VertxFutureServerInterpreter
//     import sttp.tapir.server.vertx.VertxFutureServerInterpreter.*
//     import io.vertx.core.Vertx
//     import io.vertx.ext.web.*
//     import scala.concurrent.{Await, Future}
//     import scala.concurrent.duration.*
//     val vertx = Vertx.vertx()
//     val server = vertx.createHttpServer()
//     val router = Router.router(vertx)
//     val interpreter = VertxFutureServerInterpreter()
//     def attaches = routes.map(interpreter.route(_))
//     attaches.foreach(_(router))
//     Await.result(
//       server.requestHandler(router).listen(port).asScala,
//       Duration.Inf
//     )
//   }
//   def routes(
//       routes: sttp.tapir.server.serverendpoint[
//         sttp.tapir.server.vertx.streams.vertxstreams &
//           sttp.capabilities.websockets,
//         scala.concurrent.future
//       ]*
//   ) = Server(routes ++ this.routeGroup)
// }
