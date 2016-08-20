package lv.latcraft.event.tasks

import com.amazonaws.services.lambda.runtime.Context

import static lv.latcraft.event.integrations.Configuration.sendGridDefaultListId

class CopyContactsFromEventBriteToSendGrid extends BaseTask {

  Map<String, String> execute(Map<String, String> input, Context context) {
    println "STEP 1: Received data: ${input}"
    attendees.collate(1000).each { batch ->
      sendGrid.post("/v3/contactdb/recipients", batch) { Map data ->
        reportResult(data)
        sendGrid.post("/v3/contactdb/lists/${sendGridDefaultListId}/recipients", data.persisted_recipients)
      }
    }
    [:]
  }

  List<Map<String, ?>> getAttendees() {
    uniqueAttendees(allAttendees()).collect { fromEventBriteToSendGrid(it) }
  }

  void reportResult(Map data) {
    println "STEP 2: New contacts: ${data.new_count}"
    if (data.new_count.toString().toLong() > 0) {
      slack.send("New contacts discovered, master! (${data.new_count})")
    }
    handleErrors(data)
  }

  void handleErrors(data) {
    if (data.errors) {
      println "STEP 2: Errors: ${data.error_count}"
      slack.send("I'm sorry, master, there are some errors found during contact import! (${data.errors.size()})")
      data.errors.each { error ->
        println "STEP 2: Error: ${error.message}"
      }
    }
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
    attendees.findAll { Map attendee ->
      attendee.profile.email
    }.collectEntries { Map attendee ->
      [attendee.profile.email.toLowerCase(), attendee]
    }.values()
  }

  List<Map<String, ?>> allAttendees() {
    List<Map<String, ?>> attendees = []
    eventBrite.events.findAll { Map eventBriteEvent ->
      !eventBriteEvent.name.text.toLowerCase().startsWith('devternity')
    }.each { Map eventBriteEvent ->
      println "STEP 2: Extracting attendees from: ${eventBriteEvent.name.text}"
      attendees.addAll(eventBrite.getAttendees(eventBriteEvent.id as String))
    }
    attendees
  }

}
