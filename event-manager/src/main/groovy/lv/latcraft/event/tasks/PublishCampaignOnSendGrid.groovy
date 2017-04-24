package lv.latcraft.event.tasks

import com.amazonaws.services.lambda.runtime.Context
import groovy.util.logging.Log4j
import lv.latcraft.event.integrations.Configuration
import lv.latcraft.event.lambda.InternalContext
import lv.latcraft.event.utils.S3Methods

import static lv.latcraft.event.utils.Constants.templateEngine
import static lv.latcraft.event.utils.FileMethods.temporaryFile
import static lv.latcraft.event.utils.S3Methods.putRequest

@Log4j("logger")
class PublishCampaignOnSendGrid extends BaseTask {

  Map<String, String> doExecute(Map<String, String> request, Context context) {
    Map<String, String> response = [:]
    futureEvents.each { Map event ->

      // Prepare invitation data.
      String eventId = calculateEventId(event)
      String invitationCampaignTitle = calculateInvitationCampaignTitle(event)
      String invitationCampaignContent = createHtmlDescription(event)

      Map<String, ?> campaign = sendGrid.findCampaignByTitle(invitationCampaignTitle)
      if (!campaign || campaign.status == 'Draft') {

        // Publish invitation campaign on SendGrid.
        sendGrid.updateCampaignContent(
          title               : invitationCampaignTitle,
          subject             : "Personal Invitation to \"Latcraft | ${event.theme}\"".toString(),
          sender_id           : Configuration.sendGridDefaultSenderId,
          suppression_group_id: Configuration.sendGridDefaultUnsubscribeGroupId,
          list_ids            : [Configuration.sendGridDefaultListId],
          html_content        : invitationCampaignContent
        )

        slack.send("Master, you are great! SendGrid campaign has been published (or updated) for \"${invitationCampaignTitle}\"!")

        if (!validateRequiredFields(event)) {
          slack.send("WARNING: Master, some of the required fields are missing in the event data. You RISK sending an invalid e-mail!")
        }

        // Save invitation campaign HTML on S3.
        File localFile = temporaryFile("invitation_${eventId}", ".html")
        localFile.text = invitationCampaignContent
        S3Methods.s3.putObject(putRequest("invitation_${eventId}.html", localFile))
        response['url'] = S3Methods.getObjectUrl("invitation_${eventId}.html")

        slack.send("Please, verify the invitation content: ${response['url']}!")

      } else {
        slack.send("Master, I'm sorry, but campaign \"${invitationCampaignTitle}\" has already been started and therefore cannot be updated! ")
      }

    }
    response
  }

  private static String createHtmlDescription(Map event) {
    String eventId = calculateEventId(event)
    String defaultTemplate = getRemoteFileContents(new URL("${Configuration.newsletterTemplateBaseDir}/invitation.html"))
    String overriddenTemplate = getRemoteFileContents(new URL("${Configuration.newsletterTemplateBaseDir}/invitation_${eventId}.html"))
    def template = templateEngine.createTemplate(overriddenTemplate ?: defaultTemplate)
    template.make([event: event]).toString()
  }

  static void main(String[] args) {
    new PublishCampaignOnSendGrid().execute([:], new InternalContext())
  }

}
