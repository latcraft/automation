package lv.latcraft.event.clients

import groovyx.net.http.Method

import static groovyx.net.http.Method.GET
import static lv.latcraft.event.Utils.dumpJson
import static lv.latcraft.event.clients.Configuration.getEventbriteToken

class EventBrite extends BaseJsonClient {

  String getEvents() {
    execute(GET, '/v3/users/me/owned_events', [:], 1) { data ->
      return dumpJson(data)
    }
  }

  def execute(Method method, String path, Map jsonBody, int pageNumber, Closure cl) {
    uri = 'https://www.eventbriteapi.com/'
    ignoreSSLIssues()
    makeRequest(method) {
      uri.path = "${path}"
      uri.query = [token: eventbriteToken, page: pageNumber]
      if (jsonBody) {
        log.debug dumpJson(jsonBody)
        body = jsonBody
      }
      response.success = { _, json ->
        if (cl) {
          return cl.call(json)
        }
      }
      response.failure = { resp ->
        throw new RuntimeException("Error details: ${resp.statusLine.statusCode} : ${resp.statusLine.reasonPhrase} : ${resp?.entity?.content?.text}")
      }
    }
  }

}
