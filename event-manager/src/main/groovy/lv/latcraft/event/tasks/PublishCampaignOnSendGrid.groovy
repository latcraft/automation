package lv.latcraft.event.tasks

import com.amazonaws.services.lambda.runtime.Context

import static lv.latcraft.event.Constants.templateEngine
import static lv.latcraft.event.integrations.Configuration.*

class PublishCampaignOnSendGrid extends BaseTask {

  Map<String, String> execute(Map<String, String> input, Context context) {
    // TODO: generate campaign invitation from template (read template from classpath, filesystem, github, url inside event data)
    // TODO: publish result on S3
    // TODO: syncronize campaign with sendgrid
    // TODO: update github data with link to s3
    futureEvents.each { Map event ->
      String eventId = calculateEventId(event)

      // Generate invitation template.
      File invitationTemplateFile = new File('templates/invitation.html')
      File overridenInvitationTemplateFile = new File("templates/invitation_${eventId}.html")

      def template = templateEngine.createTemplate(overridenInvitationTemplateFile.exists() ? overridenInvitationTemplateFile : invitationTemplateFile)
      new File("invitation_${eventId}.html").text = template.make(event: event).toString()

      String invitationCampaignTitle = "LatCraft ${event.theme} Invitation ${eventId}".toString()
      sendGrid.updateCampaignContent([
        title               : invitationCampaignTitle,
        subject             : "Personal Invitation to \"Latcraft | ${event.theme}\"".toString(),
        sender_id           : sendGridDefaultSenderId,
        suppression_group_id: sendGridDefaultUnsubscribeGroupId,
        list_ids            : [sendGridDefaultListId],
        html_content        : new File("invitation_${eventId}.html").text
      ])

    }
    // TODO: return link to s3
    [:]
  }

}
