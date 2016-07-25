package lv.latcraft.event.tasks

import static groovyx.net.http.Method.POST
import static lv.latcraft.event.integrations.Configuration.sendGridDefaultListId
import static lv.latcraft.event.utils.FileMethods.temporaryFile

class CopyContactsFromEventBriteToSendGrid extends BaseTask {

  void execute() {
    File contactFile = temporaryFile("contacts", ".csv")
    contactFile.withWriter { BufferedWriter writer ->
      writer << "company;email;first_name;last_name;name;job_title\n"
      def attendees = []
      eventBrite.events.findAll { !it.name.text.toLowerCase().startsWith('devternity') }.each { eventBriteEvent ->
        println "> Extracting attendees from: ${eventBriteEvent.name.text}"
        attendees.addAll(eventBrite.getAttendees(eventBriteEvent.id as String))
      }
      def filteredAttendees = [:] as TreeMap
      attendees.each { attendee ->
        if (attendee.profile.email) {
          filteredAttendees.put(attendee.profile.email.toLowerCase(), attendee)
        }
      }
      filteredAttendees.values().each { Map attendee ->
        writer << "${attendee.profile.company ?: ''};${attendee.profile.email ?: ''};${attendee.profile.first_name ?: ''};${attendee.profile.last_name ?: ''};${attendee.profile.name ?: ''};${attendee.profile.job_title ?: ''};\n"
      }
    }
    def fields = ['company', 'email', 'first_name', 'last_name', 'name', 'job_title']
    // TODO: ensure custom fields are created
    contactFile.readLines().drop(1).collate(1000).each { batch ->
      sendGrid.execute(POST, "/v3/contactdb/recipients", batch.collect {
        [fields, it.split(';')].transpose().collectEntries { it }
      }) { data ->
        println "> Errors: ${data.error_count}"
        println "> New contacts: ${data.new_count}"
        // TODO: notify slack about new count
        if (data.errors) {
          data.errors.each { error ->
            // TODO: notify slack about errors
            println "> Error: ${error.message}"
          }
        }
        sendGrid.execute(POST, "/v3/contactdb/lists/${sendGridDefaultListId}/recipients", data.persisted_recipients) { listData ->
          // log.debug listData.toString()
        }
      }
    }
  }

}
