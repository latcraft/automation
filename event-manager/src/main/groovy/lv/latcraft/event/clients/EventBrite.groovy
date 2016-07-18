package lv.latcraft.event.clients

import groovy.util.logging.Log4j
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method

import static groovyx.net.http.ContentType.JSON

@Log4j
class EventBrite {

  def execute(Method method, String path, Map jsonBody, int pageNumber, Closure cl) {
    def http = new HTTPBuilder('https://www.eventbriteapi.com/')
    http.ignoreSSLIssues()
    http.request(method, JSON) {
      uri.path = "${path}"
      uri.query = [token: latcraftEventbriteToken, page: pageNumber]
      if (jsonBody) {
        log.debug dumpJson(jsonBody)
        body = jsonBody
      }
      response.success = { _, json ->
        if (cl) {
          cl(json)
        }
      }
      response.failure = { resp ->
        throw new RuntimeException("Error details: ${resp.statusLine.statusCode} : ${resp.statusLine.reasonPhrase} : ${resp?.entity?.content?.text}")
      }
    }
  }

//
//  task getEventBriteData << {
//    buildDir.mkdirs()
//    eventbrite(GET, '/v3/users/me/owned_events', [:], 1) { data ->
//      eventbriteFile.text = dumpJson(data)
//      // TODO: handle pagination
//    }
//  }
//

}
