package lv.latcraft.event.clients

import groovy.util.logging.Log4j
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method

import static groovyx.net.http.ContentType.JSON

import static lv.latcraft.event.clients.Configuration.*
import static lv.latcraft.event.Utils.*

@Log4j
class GitHub {

  def execute(Method method, String path, Map jsonBody, Closure cl) {
    def http = new HTTPBuilder('https://api.github.com')
    http.ignoreSSLIssues()
    http.request(method, JSON) {
      headers['Content-Type'] = 'application/json'
      headers['Accept'] = 'application/vnd.github.v3+json'
      headers['User-Agent'] = 'Groovy HTTPBuilder'
      uri.path = "${path}"
      uri.query = [access_token: latcraftGitHubToken]
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
//  task updateMasterData(dependsOn: getMasterData) << {
//    String checksum = github(GET, '/repos/latcraft/website/contents/data/events.json', [:]) { data ->
//      return data.sha
//    }
//    String content = eventFile.bytes.encodeBase64().toString()
//    github(PUT, '/repos/latcraft/website/contents/data/events.json', [
//      message: "updating event data",
//      committer: [
//        name: "Latcraft Event Manager",
//        email: "hello@latcraft.lv"
//      ],
//      content: content,
//      sha: checksum
//    ]) { data ->
//      logger.debug data.toString()
//    }
//  }
//

//  String checksum = github(GET, '/repos/latcraft/website/contents/data/events.json', [:]) { data ->
//    return data.sha
//  }
//  String content = eventFile.bytes.encodeBase64().toString()
//  github(PUT, '/repos/latcraft/website/contents/data/events.json', [
//  message: "updating event data",
//  committer: [
//  name: "Latcraft Event Manager",
//  email: "hello@latcraft.lv"
//  ],
//  content: content,
//  sha: checksum
//  ]) { data ->
//    logger.debug data.toString()
//  }


}