package lv.latcraft.event.tasks

import com.amazonaws.services.lambda.runtime.Context

import static lv.latcraft.event.integrations.Configuration.sendGridDefaultListId

class CopyContactsFromEventBriteToSendGrid extends BaseTask {

  Map<String, String> execute(Map<String, String> input, Context context) {
    uniqueAttendees(findAllAttendees()).collect { fromEventBriteToSendGrid(it) }.collate(1000).each { batch ->
      sendGrid.post("/v3/contactdb/recipients", batch) { data ->
        println "> Errors: ${data.error_count}"
        println "> New contacts: ${data.new_count}"
        // TODO: notify slack about new count
        if (data.errors) {
          data.errors.each { error ->
            // TODO: notify slack about errors
            println "> Error: ${error.message}"
          }
        }
        sendGrid.post("/v3/contactdb/lists/${sendGridDefaultListId}/recipients", data.persisted_recipients)
      }
    }
    [:]
  }

  static Map<String, ?> fromEventBriteToSendGrid(Map attendee) {
    [
      company   : attendee.profile.company ?: '',
      email     : attendee.profile.email ?: '',
      first_name: attendee.profile.first_name ?: '',
      last_name : attendee.profile.last_name ?: '',
      name      : attendee.profile.name ?: '',
      job_title : attendee.profile.job_title ?: ''
    ]
  }

  static List<Map<String, ?>> uniqueAttendees(List<Map<String, ?>> attendees) {
    def filteredAttendees = [:] as TreeMap
    attendees.each { Map attendee ->
      if (attendee.profile.email) {
        filteredAttendees.put(attendee.profile.email.toLowerCase(), attendee)
      }
    }
    filteredAttendees.values()
  }

  List<Map<String, ?>> findAllAttendees() {
    List<Map<String, ?>> attendees = []
    eventBrite.events.findAll { !it.name.text.toLowerCase().startsWith('devternity') }.each { eventBriteEvent ->
      println "> Extracting attendees from: ${eventBriteEvent.name.text}"
      attendees.addAll(eventBrite.getAttendees(eventBriteEvent.id as String))
    }
    attendees
  }

}
