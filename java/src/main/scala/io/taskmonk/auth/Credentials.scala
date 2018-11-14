package io.taskmonk.auth

import com.google.inject.ImplementedBy
import com.softwaremill.sttp.RequestT

@ImplementedBy(classOf[ApiKeyCredentials])
trait Credentials {
  def addAuthInfo[U[_], T, S](request: RequestT[U, T, S]): RequestT[U, T, S]
}

class ApiKeyCredentials(apiKey: String) extends Credentials {
  def addAuthInfo[U[_], T, S] (request: RequestT[U, T, S]): RequestT[U, T, S] =  {
    request.header("Authorization",  s"API_KEY ${apiKey}")
  }
//  override def addAuthInfo(request: WSRequest): WSRequest = {
//
//    request.withHttpHeaders(("Authorization", apiKey))
//    return request
//
//  }


}
