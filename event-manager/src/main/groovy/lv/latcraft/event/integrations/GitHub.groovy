package lv.latcraft.event.integrations

import groovyx.net.http.Method

import static groovyx.net.http.Method.GET
import static groovyx.net.http.Method.PUT
import static lv.latcraft.event.utils.JsonMethods.dumpJson
import static lv.latcraft.event.integrations.Configuration.*

class GitHub extends BaseJsonClient {

  String getChecksum(String path) {
    execute(GET, path, [:]) { data -> data.sha }
  }

  void updateFile(String path, File file) {
    String content = file.bytes.encodeBase64().toString()
    execute(PUT, path, [
      message  : "updating event data",
      committer: [
        name : gitCommitter,
        email: gitEmail
      ],
      content  : content,
      sha      : getChecksum(path)
    ]) { data ->
      log.debug data.toString()
    }
  }

  def execute(Method method, String path, Map jsonBody, Closure cl) {
    uri = 'https://api.github.com'
    ignoreSSLIssues()
    makeRequest(method) {
      headers['Content-Type'] = 'application/json'
      headers['Accept'] = 'application/vnd.github.v3+json'
      headers['User-Agent'] = 'Groovy HTTPBuilder'
      uri.path = "${path}"
      uri.query = [access_token: gitHubToken]
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