package lv.latcraft.event.tasks

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

  private EventBrite eventBrite = new EventBrite()
  private Slack slack = new Slack()
  private GitHub gitHub = new GitHub()
  private SendGrid sendGrid = new SendGrid()

  abstract void execute()

  List<Map<String, ?>> getEventBriteEvents() {
    eventBrite.eventData['events'] as List<Map<String, ?>>
  }

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
