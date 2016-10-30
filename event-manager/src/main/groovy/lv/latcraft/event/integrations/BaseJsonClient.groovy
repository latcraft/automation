package lv.latcraft.event.integrations

import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method

import static groovyx.net.http.ContentType.JSON

abstract class BaseJsonClient extends HTTPBuilder {

  def makeRequest(Method method,
                  @DelegatesTo(value = HTTPBuilder.RequestConfigDelegate, strategy = Closure.DELEGATE_FIRST) Closure config) {
    this.request(method, JSON, config)
  }

}
