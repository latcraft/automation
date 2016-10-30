package lv.latcraft.event.tasks

import com.amazonaws.services.lambda.runtime.Context
import groovy.util.logging.Log4j
import lv.latcraft.event.lambda.InternalContext

@Log4j("logger")
class SendCampaignOnSendGrid extends BaseTask {

  Map<String, String> doExecute(Map<String, String> request, Context context) {
    Map<String, String> response = [:]
    futureEvents.each { Map event ->

      String eventId = calculateEventId(event)
      String invitationCampaignTitle = "LatCraft ${event.theme} Invitation ${eventId}".toString()
      logger.info "Found campaign ${invitationCampaignTitle}"
      String campaignId = sendGrid.findCampaignIdByTitle(invitationCampaignTitle)

      // TODO: check if campaign has been already sent
      // TODO: verify that announced flag is set and if not update it
      // TODO: slack message

      if (campaignId) {
        logger.info "Starting campaign with ID: ${campaignId}"
        sendGrid.post("/v3/campaigns/${campaignId}/schedules/now".toString(), [:]) { data ->
          logger.info "Scheduling result: ${data.status}"
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
