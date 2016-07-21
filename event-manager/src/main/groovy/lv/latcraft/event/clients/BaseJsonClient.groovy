package lv.latcraft.event.clients

import groovy.util.logging.Log4j
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method

import static groovyx.net.http.ContentType.JSON

@Log4j
abstract class BaseJsonClient extends HTTPBuilder {

  void makeRequest(Method method,
                   @DelegatesTo(value = RequestConfigDelegate, strategy = Closure.DELEGATE_FIRST) Closure config) {
    this.request(method, JSON, config)
  }

}
