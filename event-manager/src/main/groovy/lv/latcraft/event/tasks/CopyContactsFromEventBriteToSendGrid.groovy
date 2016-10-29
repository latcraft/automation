package lv.latcraft.event.tasks

import com.amazonaws.services.lambda.runtime.Context

import static lv.latcraft.event.integrations.Configuration.sendGridDefaultListId

class CopyContactsFromEventBriteToSendGrid extends BaseTask {

  Map<String, String> execute(Map<String, String> input, Context context) {
    println "STEP 1: Received data: ${input}"
    attendees.collate(1000).each { inputData ->
      sendGrid.post("/v3/contactdb/recipients", inputData) { Map responseData ->
        reportResult(inputData, responseData)
        sendGrid.post("/v3/contactdb/lists/${sendGridDefaultListId}/recipients", responseData.persisted_recipients)
      }
    }
    [:]
  }

  List<Map<String, ?>> getAttendees() {
    uniqueAttendees(allAttendees()).collect { fromEventBriteToSendGrid(it) }
  }

  void reportResult(List inputData, Map responseData) {
    if (responseData.new_count.toString().toLong() > 0) {
      println "STEP 3: New contacts: ${responseData.new_count}"
      slack.send("New contacts discovered, master! (${responseData.new_count})")
    }
    handleErrors(inputData, responseData)
  }

  void handleErrors(inputData, responseData) {
    if (responseData.errors) {
      responseData.errors.each { error ->
        if (!error.message.toString().contains("Email duplicated in request")) {
          println "STEP 3: Error: ${error.message} = ${error.error_indices.size()}"
          slack.send("I'm sorry, master, there are some errors found during contact import! (${error.message} = ${error.error_indices.size()})")
          error.error_indices.each { index ->
            println "STEP 3: Error: ${inputData[index].email}"
          }
        }
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
    attendees
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

  public static void main(String[] args) {
    new CopyContactsFromEventBriteToSendGrid().execute([:], null)
  }

}
