package lv.latcraft.event.integrations

import groovy.util.logging.Log4j
import groovyx.net.http.Method

import static groovyx.net.http.Method.GET
import static groovyx.net.http.Method.POST
import static groovyx.net.http.Method.POST
import static lv.latcraft.event.integrations.Configuration.eventbriteToken
import static lv.latcraft.event.utils.JsonMethods.dumpJson
import static lv.latcraft.event.integrations.Configuration.getEventbriteToken

@Log4j("logger")
class EventBrite extends BaseJsonClient {

  Map<String, ?> getVenueData(String venueId) {
    execute(GET, "/v3/venues/${venueId}", [:], 1) { data -> data } as Map<String, ?>
  }

  Map<String, ?> getEventData() {
    execute(GET, '/v3/users/me/owned_events', [:], 1) { data -> data } as Map<String, ?>
  }

  List<Map<String, ?>> getEvents() {
    eventData['events'] as List<Map<String, ?>>
  }

  List<Map<String, ?>> getAttendees(String eventId) {
    def attendees = []
    execute(GET, "/v3/events/${eventId}/attendees/".toString(), [:], 1) { data ->
      attendees.addAll(data.attendees as List)
      for (int pageNumber = 1; data.pagination.page_count >= pageNumber; pageNumber++) {
        execute(GET, "/v3/events/${eventId}/attendees/".toString(), [:], pageNumber) { pageData ->
          attendees.addAll(pageData.attendees as List)
        }
      }
    }
    attendees
  }

  def get(String path, Closure cl) {
    execute(GET, path, [:], 1, cl)
  }

  def post(String path) {
    execute(POST, path, [:], 1, null)
  }

  def post(String path, Closure cl) {
    execute(POST, path, [:], 1, cl)
  }

  def post(String path, jsonBody) {
    execute(POST, path, jsonBody, 1, null)
  }

  def post(String path, jsonBody, Closure cl) {
    execute(POST, path, jsonBody, 1, cl)
  }

  def execute(Method method, String path, jsonBody, int pageNumber, Closure cl) {
    uri = 'https://www.eventbriteapi.com/'
    ignoreSSLIssues()
    makeRequest(method) {
      uri.path = "${path}"
      uri.query = [token: eventbriteToken, page: pageNumber]
      if (jsonBody) {
        logger.debug dumpJson(jsonBody)
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
