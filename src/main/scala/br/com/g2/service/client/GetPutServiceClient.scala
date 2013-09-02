package br.com.g2.service.client

import com.twitter.finagle.Service
import com.twitter.finagle.thrift.ThriftClientRequest
import com.twitter.finagle.builder.ClientBuilder
import java.net.InetSocketAddress
import com.twitter.finagle.thrift.ThriftClientFramedCodec
import br.com.g2.service.GetPutService
import org.apache.thrift.protocol._

object GetPutServiceClient {

  def main(args: Array[String]) {
    val service: Service[ThriftClientRequest, Array[Byte]] =
      ClientBuilder()
        .hosts(new InetSocketAddress("localhost",8889))
        .codec(ThriftClientFramedCodec())
        .hostConnectionLimit(1)
        .build()

    val client = new GetPutService.FinagledClient(service, new TBinaryProtocol.Factory())
    client.get("key1") onSuccess {
      response => println("Response found: ", response)
    } onFailure { exc =>
      exc.printStackTrace()
    } ensure {
      service.close()
    }

  }
}