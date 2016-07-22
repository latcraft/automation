package lv.latcraft.event.tasks

class SendCampaignOnSendGrid extends BaseTask {

  void execute() {

  }

//
//
//task startInvitationCampaign << {
//  getFutureEvents().each { event ->
//
//    String eventId = calculateEventId(event)
//    String invitationCampaignTitle = "LatCraft ${event.theme} Invitation ${eventId}".toString()
//    logger.quiet "> Starting ${invitationCampaignTitle}"
//
//    String campaignId = findCampaignIdByTitle(invitationCampaignTitle)
//    if (campaignId) {
//      logger.quiet "> Starting campaign with ID: ${campaignId}"
//      sendgrid(POST, "/v3/campaigns/${campaignId}/schedules/now".toString(), [:]) { data ->
//        logger.quiet "> Scheduling result: ${data.status}"
//      }
//    } else {
//      throw new GradleException("Campaign \"${invitationCampaignTitle}\" not found!")
//    }
//
//  }
//}
//
//task build(dependsOn: [ synchronizeCampaigns, synchronizeContactList ])

}
