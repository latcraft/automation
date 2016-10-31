package lv.latcraft.event.integrations

import groovy.json.JsonBuilder
import groovy.util.logging.Log4j

import static groovyx.net.http.Method.POST
import static lv.latcraft.event.integrations.Configuration.defaultSlackHookUrl
import static lv.latcraft.event.utils.JsonMethods.dumpJson

@Log4j("logger")
class Slack extends BaseJsonClient {

  def send(String message, String userName = null, String icon = null, String channel = null) {
    uri = defaultSlackHookUrl
    def payload = [
      text        : message,
      username    : userName,
      'icon-emoji': icon,
      channel     : channel
    ]
    logger.debug dumpJson(payload)
    makeRequest(POST) {
      contentType = 'text/plain'
      body = new JsonBuilder(payload).toString()
      response.success = { _, response ->
        response.text
      }
      response.failure = { resp ->
        throw new RuntimeException("Error details: ${resp.statusLine.statusCode} : ${resp.statusLine.reasonPhrase} : ${resp?.entity?.content?.text}")
      }
    }
  }

}
