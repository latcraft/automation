package lv.latcraft.event.tasks

class SendSendGridCampaign {

//
//  defaultSenderId = "37076"
//  defaultListId = "362055"
//  defaultUnsubscribeGroupId = "611"
//
//  contactFile = file("${buildDir}/contacts.csv")
//
//  // TODO: convert sendgrid client to a class
//
//  sendgrid = { Method method, String path, jsonBody, Closure cl ->
//    def http = new HTTPBuilder('https://api.sendgrid.com')
//    http.ignoreSSLIssues()
//    http.request(method, JSON) {
//      headers.'Content-Type' = 'application/json'
//      headers.'User-Agent' = 'curl/7.9.8 (i686-pc-linux-gnu) libcurl 7.9.8 (OpenSSL 0.9.6b) (ipv6 enabled)'
//      headers.'Authorization' = "Bearer ${latcraftSendGridApiKey}"
//      uri.path = "${path}"
//      if (jsonBody) {
//        logger.debug dumpJson(jsonBody)
//        body = jsonBody
//      }
//      response.success = { _, json ->
//        if (cl) {
//          cl(json)
//        }
//      }
//      response.failure = { resp ->
//        throw new GradleException("Error details: ${resp.statusLine.statusCode} : ${resp.statusLine.reasonPhrase} : ${resp?.entity?.content?.text}")
//      }
//    }
//  }
//
//  findCampaignIdByTitle = { String campaignTitle ->
//    sendgrid(GET, '/v3/campaigns', [:]) { data ->
//      data.result.find { campaign -> campaign.title == campaignTitle }?.id
//    }
//  }
//
//  updateCampaignContent = { Map content ->
//    logger.quiet "> Preparing to create/update \"${content.title}\""
//    String campaignId = findCampaignIdByTitle(content.title)
//    sleep(1000)
//    if (campaignId) {
//      logger.quiet "> Updating campaign with ID: ${campaignId}"
//      return sendgrid(PATCH, "/v3/campaigns/${campaignId}".toString(), content) { data ->
//        data.id
//      }
//    } else {
//      return sendgrid(POST, "/v3/campaigns", content) { data ->
//        data.id
//      }
//    }
//  }
//
//  findTemplateIdByName = { String templateName ->
//    sendgrid(GET, '/v3/templates', [:]) { data ->
//      data.templates.find { template -> template.name == templateName }?.id
//    }
//  }
//
//  getTemplateVersionId = { String templateId ->
//    sendgrid(GET, "/v3/templates/${templateId}", [:]) { data ->
//      data.versions.find { templateVersion -> templateVersion.active == 1 }?.id
//    }
//  }
//
//  updateTemplateContent = { String templateId, Map content ->
//    String templateVersionId = getTemplateVersionId(templateId)
//    if (templateVersionId) {
//      sendgrid(PATCH, "/v3/templates/${templateId}/versions/${templateVersionId}".toString(), content) { data ->
//        logger.debug data.toString()
//      }
//    } else {
//      sendgrid(POST, "/v3/templates/${templateId}/versions".toString(), content) { data ->
//        logger.debug data.toString()
//      }
//    }
//  }
//
//}
//
//task extractContactsFromEventBrite(dependsOn: getEventBriteData) << {
//  contactFile.withWriter { Writer writer ->
//    writer << "company;email;first_name;last_name;name;job_title\n"
//    def attendees = []
//    getEventBriteEvents().findAll{ !it.name.text.toLowerCase().startsWith('devternity') }.each { eventBriteEvent ->
//      logger.quiet "> Extracting attendees from: ${eventBriteEvent.name.text}"
//      eventbrite(GET, "/v3/events/${eventBriteEvent.id}/attendees/".toString(), [:], 1) { data ->
//        attendees.addAll(data.attendees)
//        for(int pageNumber = 1; pageNumber <= data.pagination.page_count; pageNumber++) {
//          eventbrite(GET, "/v3/events/${eventBriteEvent.id}/attendees/".toString(), [:], pageNumber) { pageData ->
//            attendees.addAll(pageData.attendees)
//          }
//        }
//      }
//    }
//    def filteredAttendees = [:] as TreeMap
//    attendees.each { attendee ->
//      if (attendee.profile.email) {
//        filteredAttendees.put(attendee.profile.email.toLowerCase(), attendee)
//      }
//    }
//    filteredAttendees.values().each { attendee ->
//      writer << "${attendee.profile.company ?: ''};${attendee.profile.email ?: ''};${attendee.profile.first_name ?: ''};${attendee.profile.last_name ?: ''};${attendee.profile.name ?: ''};${attendee.profile.job_title ?: ''};\n"
//    }
//  }
//}
//
//
//extractContactsFromEventBrite.outputs.file contactFile
//
//
//task synchronizeContactList(dependsOn: extractContactsFromEventBrite) << {
//  def fields = [ 'company', 'email', 'first_name', 'last_name', 'name', 'job_title' ]
//  // TODO: ensure custom fields are created
//  contactFile.readLines().drop(1).collate(1000).each { batch ->
//    sendgrid(POST, "/v3/contactdb/recipients", batch.collect{ [ fields, it.split(';') ].transpose().collectEntries{ it } }) { data ->
//      logger.quiet "> Errors: ${data.error_count}"
//      logger.quiet "> New contacts: ${data.new_count}"
//      if (data.errors) {
//        data.errors.each { error ->
//          logger.quiet "> Error: ${error.message}"
//        }
//      }
//      sendgrid(POST, "/v3/contactdb/lists/${defaultListId}/recipients", data.persisted_recipients) { listData ->
//        logger.debug listData.toString()
//      }
//    }
//  }
//}
//
//
//task generateCampaignTemplates(dependsOn: getMasterData) << {
//  getEvents().each { event ->
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
//
//task startInvitionCampaign << {
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
