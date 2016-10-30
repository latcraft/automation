package lv.latcraft.event.lambda

import com.amazonaws.services.lambda.runtime.Client
import com.amazonaws.services.lambda.runtime.ClientContext

class InternalClientContext implements ClientContext {

  Client getClient() {
    new InternalClient()
  }

  Map<String, String> getCustom() {
    [:]
  }

  Map<String, String> getEnvironment() {
    [:]
  }

}
