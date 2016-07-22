package lv.latcraft.event.tasks

class PublishCampaignOnSendGrid extends BaseTask {

  void execute() {

  }

  //task generateCampaignTemplates(dependsOn: getMasterData) << {
//  getEventData().each { event ->
//
//    String eventId = calculateEventId(event)
//    def binding = [ event: event ]
//
//    // Generate invitation template.
//    File invitationTemplateFile = file('templates/invitation.html')
//    File overridenInvitationTemplateFile = file("templates/invitation_${eventId}.html")
//    def template = templateEngine.createTemplate(overridenInvitationTemplateFile.exists() ? overridenInvitationTemplateFile : invitationTemplateFile)
//    file("${buildDir}/invitation_${eventId}.html").text = template.make(binding).toString()
//
//  }
//}
//
//
//task synchronizeCampaigns(dependsOn: [ getMasterData, generateCampaignTemplates ]) << {
//  getFutureEvents().each { event ->
//
//    String eventId = calculateEventId(event)
//
//    // Create invitation campaign.
//    String invitationCampaignTitle = "LatCraft ${event.theme} Invitation ${eventId}".toString()
//    String invitationCampaignId = updateCampaignContent([
//      title: invitationCampaignTitle,
//      subject: "Personal Invitation to \"Latcraft | ${event.theme}\"".toString(),
//      sender_id: defaultSenderId,
//      suppression_group_id: defaultUnsubscribeGroupId,
//      list_ids: [ defaultListId ],
//      html_content: file("${buildDir}/invitation_${eventId}.html").text
//    ])
//
//  }
//}
//


}
