package lv.latcraft.event.clients

class SendGrid extends BaseJsonClient {



  //  sendgrid = { Method method, String path, jsonBody, Closure cl ->
//    def http = new HTTPBuilder('https://api.sendgrid.com')
//    http.ignoreSSLIssues()
//    http.request(method, JSON) {
//      headers.'Content-Type' = 'application/json'
//      headers.'User-Agent' = 'curl/7.9.8 (i686-pc-linux-gnu) libcurl 7.9.8 (OpenSSL 0.9.6b) (ipv6 enabled)'
//      headers.'Authorization' = "Bearer ${latcraftSendGridApiKey}"
//      uri.path = "${path}"
//      if (jsonBody) {
//        logger.debug dumpJson(jsonBody)
//        body = jsonBody
//      }
//      response.success = { _, json ->
//        if (cl) {
//          cl(json)
//        }
//      }
//      response.failure = { resp ->
//        throw new GradleException("Error details: ${resp.statusLine.statusCode} : ${resp.statusLine.reasonPhrase} : ${resp?.entity?.content?.text}")
//      }
//    }
//  }

}
