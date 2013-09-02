package br.com.g2.service

import com.twitter.finagle.Filter
import com.twitter.util.Duration
import com.twitter.finagle.RequestTimeoutException
import com.twitter.util.Timer
import com.twitter.finagle.IndividualRequestTimeoutException
import com.twitter.finagle.Service
import com.twitter.util.Future
import com.twitter.finagle.tracing.Trace

class InterceptFilter[Req, Rep] extends Filter[Req, Rep, Req, Rep] {

  def apply(request: Req, service: Service[Req, Rep]): Future[Rep] = {
    val res = service(request)
    println("Simple logging")
    res
  }
}

object InterceptFilter {
  val InterceptAnnotation = "interceptFilter.apply"
}