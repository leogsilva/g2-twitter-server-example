package br.com.g2.service

import com.g2.server.TwitterServer
import com.twitter.util.Future
import org.apache.thrift.protocol.TBinaryProtocol
import com.twitter.finagle.builder.Server
import com.twitter.finagle.builder.ServerBuilder
import com.twitter.finagle.thrift.ThriftServerFramedCodec
import java.net.InetSocketAddress

object GetPutServiceImpl extends TwitterServer { 

  val serviceAddress = flag("service.address", new InetSocketAddress("localhost", 8889), "Service Port")
  val serviceName = flag("service.name", "simpleThriftServer", "Service Name")

  def main() = {
    val processor = new GetPutService.FutureIface {
      def get(key: String): Future[String] = {
        return Future.value("ok")
      }

      def put(key: String, value: String): Future[Unit] = {
        Future.Done
      }
    }
    
    val service = new GetPutService.FinagledService(processor,new TBinaryProtocol.Factory)
    val mservice = new InterceptFilter() andThen service
    
    val server: Server = ServerBuilder()
      .codec(ThriftServerFramedCodec())
      .tracer(tracer)
      .bindTo(serviceAddress())
      .name(serviceName())
      .build(mservice)

    joinCluster(serviceAddress(), "/" + serviceName())    
  }
}