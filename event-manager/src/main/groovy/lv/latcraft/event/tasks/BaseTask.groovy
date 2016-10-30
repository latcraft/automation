package lv.latcraft.event.tasks

import com.amazonaws.services.lambda.runtime.Context
import groovy.json.JsonSlurper
import groovy.util.logging.Log4j
import lv.latcraft.event.integrations.EventBrite
import lv.latcraft.event.integrations.GitHub
import lv.latcraft.event.integrations.SendGrid
import lv.latcraft.event.integrations.Slack

import static lv.latcraft.event.utils.Constants.dateFormat
import static lv.latcraft.event.integrations.Configuration.eventDataFile

@Log4j("baseLogger")
abstract class BaseTask {

  EventBrite eventBrite = new EventBrite()
  Slack slack = new Slack()
  GitHub gitHub = new GitHub()
  SendGrid sendGrid = new SendGrid()

  Map<String, String> execute(Map<String, String> request, Context context) {
    Map<String, String> response = [:]
    try {
      baseLogger.info "Received request parameters: ${request}"
      response = doExecute(request, context)
    } catch (Throwable t) {
      baseLogger.error('Uncaught exception', t)
      try {
        slack.send("Sorry, master, there seems to be some error with ${this.getClass().simpleName}, it threw ${t.getClass().simpleName} at '${t.stackTrace[0]}' with message '${t.message}'")
      } catch (Throwable x) {
        baseLogger.error('Problem sending slack message', x)
      }
    } finally {
      baseLogger.info "Sending response: ${response}"
    }
    response
  }

  abstract Map<String, String> doExecute(Map<String, String> input, Context context)

  static List<Map<String, ?>> getMasterData() {
    new JsonSlurper().parse(new URL(eventDataFile).newInputStream()) as List<Map<String, ?>>
  }

  static List<Map<String, ?>> getEvents() {
    getMasterData()
  }

  static List<Map<String, ?>> getFutureEvents() {
    events.findAll { isFutureEvent(it) }
  }

  /**
   * Calculate unique event ID used to distinguish this event from others in various data sources.
   */
  static String calculateEventId(Map<String, ?> event) {
    dateFormat.parse(event['date'].toString()).format('yyyyMMdd')
  }

  static boolean isFutureEvent(Map<String, ?> event) {
    dateFormat.parse(event['date'].toString()) > new Date()
  }

}
