package lv.latcraft.event.integrations

import groovy.util.logging.Log4j
import groovyx.net.http.Method

import static groovyx.net.http.Method.*
import static lv.latcraft.event.integrations.Configuration.sendGridApiKey
import static lv.latcraft.event.utils.JsonMethods.dumpJson

@Log4j("logger")
class SendGrid extends BaseJsonClient {

  Map<String, ?> findCampaignByTitle(String campaignTitle) {
    execute(GET, '/v3/campaigns', [:]) { data ->
      data.result.find { campaign -> campaign.title == campaignTitle }
    } as Map<String, ?>
  }

  List<Map<String, ?>> getSuppressions() {
    execute(GET, '/v3/asm/suppressions', [:]) { data ->
      data
    } as List<Map<String, ?>>
  }

  List<Map<String, ?>> getGlobalUnsubscribes() {
    execute(GET, '/v3/suppression/unsubscribes', [:]) { data ->
      data
    } as List<Map<String, ?>>
  }

  List<Map<String, ?>> getInvalidEmails() {
    execute(GET, '/v3/suppression/invalid_emails', [:]) { data ->
      data
    } as List<Map<String, ?>>
  }

  List<Map<String, ?>> getSpamReports() {
    execute(GET, '/v3/suppression/spam_reports', [:]) { data ->
      data
    } as List<Map<String, ?>>
  }

  String updateCampaignContent(Map content) {
    logger.info "Preparing to create/update \"${content.title}\""
    Map<String, ?> campaign = findCampaignByTitle(content.title as String)
    sleep(1000)
    if (campaign?.id) {
      if (campaign.status == 'Draft') {
        logger.info "Updating campaign with ID: ${campaign.id}"
        return execute(PATCH, "/v3/campaigns/${campaign.id}".toString(), content) { data ->
          data.id
        }
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
        logger.debug data.toString()
      }
    } else {
      execute(POST, "/v3/templates/${templateId}/versions".toString(), content) { data ->
        logger.debug data.toString()
      }
    }
  }

  def get(String path, Closure cl) {
    execute(GET, path, [:], cl)
  }

  def post(String path, jsonBody) {
    execute(POST, path, jsonBody, null)
  }

  def post(String path, jsonBody, Closure cl) {
    execute(POST, path, jsonBody, cl)
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
