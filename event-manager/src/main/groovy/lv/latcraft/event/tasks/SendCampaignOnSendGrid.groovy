package lv.latcraft.event.tasks

import com.amazonaws.services.lambda.runtime.Context
import groovy.util.logging.Log4j
import lv.latcraft.event.lambda.InternalContext

@Log4j("logger")
class SendCampaignOnSendGrid extends BaseTask {

  Map<String, String> doExecute(Map<String, String> request, Context context) {
    futureEvents.each { Map event ->
      String eventId = calculateEventId(event)
      String invitationCampaignTitle = "LatCraft ${event.theme} Invitation ${eventId}".toString()
      logger.info "Found campaign ${invitationCampaignTitle}"
      String campaignId = sendGrid.findCampaignIdByTitle(invitationCampaignTitle)
      if (campaignId) {
        logger.info "Starting campaign with ID: ${campaignId}"
        sendGrid.post("/v3/campaigns/${campaignId}/schedules/now".toString(), [:]) { data ->
          logger.info "Scheduling result: ${data.status}"
        }
      } else {
        throw new RuntimeException("Campaign \"${invitationCampaignTitle}\" not found!")
      }
    }
    [:]
  }

  public static void main(String[] args) {
    new SendCampaignOnSendGrid().execute([:], new InternalContext())
  }

}
