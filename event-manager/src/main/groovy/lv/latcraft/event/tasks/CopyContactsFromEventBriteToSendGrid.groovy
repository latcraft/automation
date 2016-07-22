package lv.latcraft.event.tasks

class CopyContactsFromEventBriteToSendGrid extends BaseTask {

  void execute() {

  }

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

  //  contactFile = file("${buildDir}/contacts.csv")
//
//
//
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
}
