package lv.latcraft.event.tasks

import com.amazonaws.services.lambda.runtime.Context
import lv.latcraft.event.lambda.InternalContext

class SendCampaignOnSendGrid extends BaseTask {

  Map<String, String> execute(Map<String, String> input, Context context) {
    log.info "STEP 1: Received data: ${input}"
    futureEvents.each { Map event ->
      String eventId = calculateEventId(event)
      String invitationCampaignTitle = "LatCraft ${event.theme} Invitation ${eventId}".toString()
      println "STEP 2: Found campaign ${invitationCampaignTitle}"
      String campaignId = sendGrid.findCampaignIdByTitle(invitationCampaignTitle)
      if (campaignId) {
        println "STEP 3: Starting campaign with ID: ${campaignId}"
        sendGrid.post("/v3/campaigns/${campaignId}/schedules/now".toString(), [:]) { data ->
          println "STEP 4: Scheduling result: ${data.status}"
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
