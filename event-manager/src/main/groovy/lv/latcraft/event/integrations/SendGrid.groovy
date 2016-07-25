package lv.latcraft.event.integrations

import groovyx.net.http.Method

import static groovyx.net.http.Method.*
import static lv.latcraft.event.integrations.Configuration.sendGridApiKey
import static lv.latcraft.event.utils.JsonMethods.dumpJson

class SendGrid extends BaseJsonClient {

  String findCampaignIdByTitle(String campaignTitle) {
    execute(GET, '/v3/campaigns', [:]) { data ->
      data.result.find { campaign -> campaign.title == campaignTitle }?.id
    }
  }

  String updateCampaignContent(Map content) {
    log.info "> Preparing to create/update \"${content.title}\""
    String campaignId = findCampaignIdByTitle(content.title as String)
    sleep(1000)
    if (campaignId) {
      log.info "> Updating campaign with ID: ${campaignId}"
      return execute(PATCH, "/v3/campaigns/${campaignId}".toString(), content) { data ->
        data.id
      }
    } else {
      return execute(POST, "/v3/campaigns", content) { data ->
        data.id
      }
    }
  }

  String findTemplateIdByName(String templateName) {
    execute(GET, '/v3/templates', [:]) { data ->
      data.templates.find { template -> template.name == templateName }?.id
    }
  }

  String getTemplateVersionId(String templateId) {
    execute(GET, "/v3/templates/${templateId}", [:]) { data ->
      data.versions.find { templateVersion -> templateVersion.active == 1 }?.id
    }
  }

  void updateTemplateContent(String templateId, Map content) {
    String templateVersionId = getTemplateVersionId(templateId)
    if (templateVersionId) {
      execute(PATCH, "/v3/templates/${templateId}/versions/${templateVersionId}".toString(), content) { data ->
        log.debug data.toString()
      }
    } else {
      execute(POST, "/v3/templates/${templateId}/versions".toString(), content) { data ->
        log.debug data.toString()
      }
    }
  }

  def execute(Method method, String path, jsonBody, Closure cl) {
    uri = 'https://api.sendgrid.com'
    ignoreSSLIssues()
    makeRequest(method) {
      headers['Content-Type'] = 'application/json'
      headers['User-Agent'] = 'curl/7.9.8 (i686-pc-linux-gnu) libcurl 7.9.8 (OpenSSL 0.9.6b) (ipv6 enabled)'
      headers['Authorization'] = "Bearer ${sendGridApiKey}"
      uri.path = "${path}"
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
