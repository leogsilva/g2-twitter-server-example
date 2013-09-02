package br.com.g2.service

import com.twitter.finagle.Service
import com.twitter.finagle.http.Http
import com.twitter.util.Future
import org.jboss.netty.handler.codec.http.{ DefaultHttpResponse, HttpVersion, HttpResponseStatus, HttpRequest, HttpResponse }
import java.net.{ SocketAddress, InetSocketAddress }
import com.twitter.finagle.builder.{ Server, ServerBuilder }
import com.twitter.finagle.builder.ServerBuilder
import com.g2.server.TwitterServer
import com.twitter.common.zookeeper.ZooKeeperClient
import com.twitter.common.quantity.Amount
import com.twitter.common.quantity.Time
import com.twitter.common.zookeeper.ServerSetImpl
import com.twitter.finagle.zookeeper.ZookeeperServerSetCluster
import com.twitter.util.Await
import com.twitter.finagle.zipkin.thrift.ZipkinTracer
import com.twitter.finagle.stats.LoadedStatsReceiver
import com.twitter.finagle.tracing.Tracer
import com.twitter.util.Duration
import java.util.concurrent.TimeUnit
import com.twitter.util.Timer
import com.twitter.util.JavaTimer
import com.twitter.finagle.tracing.Trace

object MyService extends TwitterServer { self: App =>

  val serviceAddress = flag("service.address", new InetSocketAddress("localhost", 8888), "Zookeeper Port")
  val serviceName = flag("service.name", "simpleServer", "Service Name")

  // Define our service: OK response for root, 404 for other paths
  val rootService = new Service[HttpRequest, HttpResponse] {
    def apply(request: HttpRequest) = {
      val r = request.getUri match {
        case "/" => new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK)
        case _ =>
          Thread.sleep(Duration.fromSeconds(3).inMillis)
          new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND)
      }
      Trace.record(InterceptFilter.InterceptAnnotation)
      Future.value(r)
    }
  }

  def main() = {
    // Serve our service on a port
    val mservice = new InterceptFilter() andThen rootService
    val server: Server = ServerBuilder()
      .codec(Http().enableTracing(true))
      .tracer(tracer)
      .bindTo(serviceAddress())
      .name(serviceName())
      .build(mservice)

    joinCluster(serviceAddress(), "/" + serviceName())
  }


}