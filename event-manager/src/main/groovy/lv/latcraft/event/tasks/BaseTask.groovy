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

@Log4j
abstract class BaseTask {

  EventBrite eventBrite = new EventBrite()
  Slack slack = new Slack()
  GitHub gitHub = new GitHub()
  SendGrid sendGrid = new SendGrid()

  abstract Map<String, String> execute(Map<String, String> input, Context context)

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