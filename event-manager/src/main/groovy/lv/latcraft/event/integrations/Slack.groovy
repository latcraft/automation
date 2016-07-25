package lv.latcraft.event.integrations

import static groovyx.net.http.Method.POST
import static lv.latcraft.event.integrations.Configuration.defaultSlackHookUrl

class Slack extends BaseJsonClient {

  def send(String message, String userName = null, String icon = null, String channel = null) {
    makeRequest(POST) {
      headers['Content-Type'] = 'application/json'
      uri.path = defaultSlackHookUrl
      body = [
        text : message,
        username: userName,
        'icon-emoji': icon,
        channel: channel
      ]
      response.success = { _, json ->
      }
      response.failure = { resp ->
        throw new RuntimeException("Error details: ${resp.statusLine.statusCode} : ${resp.statusLine.reasonPhrase} : ${resp?.entity?.content?.text}")
      }
    }
  }

}
