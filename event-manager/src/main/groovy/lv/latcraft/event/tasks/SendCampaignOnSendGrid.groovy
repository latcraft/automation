package lv.latcraft.event.tasks

import com.amazonaws.services.lambda.runtime.Context
import groovy.util.logging.Log4j
import lv.latcraft.event.lambda.InternalContext

@Log4j("logger")
class SendCampaignOnSendGrid extends BaseTask {

  Map<String, String> doExecute(Map<String, String> request, Context context) {
    Map<String, String> response = [:]
    futureEvents.each { Map event ->

      String invitationCampaignTitle = calculateInvitationCampaignTitle(event)
      logger.info "Found campaign ${invitationCampaignTitle}"
      Map<String, ?> campaign = sendGrid.findCampaignByTitle(invitationCampaignTitle)

      if (!event.announced) {
        logger.warn "Event is not yet announced: ${event.theme}"

        // TODO: verify that announced flag is set and if not update it
      }

      if (campaign.id) {
        if (campaign.status == 'Draft') {
          logger.info "Starting campaign with ID: ${campaign.id}"
          sendGrid.post("/v3/campaigns/${campaign.id}/schedules/now".toString(), [:]) { data ->
            logger.info "Scheduling result: ${data.status}"
          }
          slack.send("Master, as always you did a great job! Campaign \"${invitationCampaignTitle}\" has been scheduled to start NOW! ")
        } else {
          logger.info "Campaign \"${invitationCampaignTitle}\" has already been sent."
          slack.send("Master, it is good that you've checked, but campaign \"${invitationCampaignTitle}\" has already been started! ")
        }
      } else {
        throw new RuntimeException("Campaign \"${invitationCampaignTitle}\" not found!")
      }

    }
    response
  }

  public static void main(String[] args) {
    new SendCampaignOnSendGrid().execute([:], new InternalContext())
  }

}
