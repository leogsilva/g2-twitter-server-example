package br.com.g2.service

import com.g2.server.TwitterServer
import com.twitter.finagle.{Http, ListeningServer, NullServer, Service}
import com.twitter.util.{Await, Future}
import org.jboss.netty.buffer.ChannelBuffers.copiedBuffer
import org.jboss.netty.handler.codec.http._
import org.jboss.netty.util.CharsetUtil.UTF_8
import com.twitter.finagle.http.HttpMuxer
import java.net.InetSocketAddress

object MyService extends TwitterServer {

  val httpServicePort = flag("service.port", new InetSocketAddress(findFreePort), "Http server port")

  
  val service = new Service[HttpRequest, HttpResponse] {
    def apply(request: HttpRequest) = {
      val response =
        new DefaultHttpResponse(request.getProtocolVersion, HttpResponseStatus.OK)
      response.setContent(copiedBuffer("hello", UTF_8))
      Future.value(response)
    }
  }
  
  def main() = {
    val muxer = HttpMuxer
    
    muxer.addHandler("/app", service)
    val port = httpServicePort()
    println("Starting service on ", port)
    val server = Http.serve(port, muxer)
    onExit {
      server.close()
    }
    Await.ready(server)
  }
}