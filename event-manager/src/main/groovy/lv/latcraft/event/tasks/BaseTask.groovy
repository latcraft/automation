package lv.latcraft.event.tasks

import groovy.util.logging.Log4j
import lv.latcraft.event.clients.EventBrite
import lv.latcraft.event.clients.GitHub
import lv.latcraft.event.clients.SendGrid
import lv.latcraft.event.clients.Slack

import static lv.latcraft.event.Constants.dateFormat

@Log4j
class BaseTask {

  private EventBrite eventBrite = new EventBrite()
  private Slack slack = new Slack()
  private GitHub gitHub = new GitHub()
  private SendGrid sendGrid = new SendGrid()

  List<Map<String, ?>> getEvents() {
    eventBrite.eventData['events'] as List<Map<String, ?>>
  }

  List<Map<String, ?>> getFutureEvents() {
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

  static File file(String path) {
    new File(path)
  }

}
