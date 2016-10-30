package lv.latcraft.event.tasks

import com.amazonaws.services.lambda.runtime.Context
import groovy.util.logging.Log4j
import lv.latcraft.event.integrations.Configuration
import lv.latcraft.event.lambda.InternalContext

import static lv.latcraft.event.Constants.templateEngine
import static lv.latcraft.event.Constants.templateEngine
import static lv.latcraft.event.integrations.Configuration.*

@Log4j("logger")
class PublishCampaignOnSendGrid extends BaseTask {

  Map<String, String> doExecute(Map<String, String> request, Context context) {
    futureEvents.each { Map event ->
      String eventId = calculateEventId(event)
      String invitationCampaignTitle = "LatCraft ${event.theme} Invitation ${eventId}".toString()
      sendGrid.updateCampaignContent(
        title               : invitationCampaignTitle,
        subject             : "Personal Invitation to \"Latcraft | ${event.theme}\"".toString(),
        sender_id           : Configuration.sendGridDefaultSenderId,
        suppression_group_id: Configuration.sendGridDefaultUnsubscribeGroupId,
        list_ids            : [Configuration.sendGridDefaultListId],
        html_content        : createHtmlDescription(event)
      )
      slack.send("Master, you are great! SendGrid campaign has been published (or updated) for \"Latcraft | ${event.theme}\"!")
      // TODO: publish campaign invitation HTML result on S3
      // TODO: update gitHub data with link to s3
      // TODO: return link to s3
    }
    [:]
  }

  private static String createHtmlDescription(Map event) {
    String eventId = calculateEventId(event)
    String defaultTemplate = getRemoteFileContents(new URL("${Configuration.newsletterTemplateBaseDir}/invitation.html"))
    String overriddenTemplate = getRemoteFileContents(new URL("${Configuration.newsletterTemplateBaseDir}/invitation_${eventId}.html"))
    def template = templateEngine.createTemplate(overriddenTemplate ?: defaultTemplate)
    template.make([event: event]).toString()
  }

  public static void main(String[] args) {
    new PublishCampaignOnSendGrid().execute([:], new InternalContext())
  }

}
