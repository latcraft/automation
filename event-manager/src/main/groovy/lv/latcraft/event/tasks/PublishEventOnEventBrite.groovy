package lv.latcraft.event.tasks

import com.amazonaws.services.lambda.runtime.Context
import groovy.json.JsonSlurper
import lv.latcraft.event.Constants
import lv.latcraft.event.lambda.InternalContext

import static lv.latcraft.event.Constants.dateFormat
import static lv.latcraft.event.Constants.isoDateFormat

class PublishEventOnEventBrite extends BaseTask {

  Map<String, String> execute(Map<String, String> input, Context context) {
    log.info "STEP 1: Received data: ${input}"
    File defaultTemplateFile = new File('templates/event_description.html')
    eventBrite.events.each { Map event ->
      String eventId = dateFormat.parse(event.date as String).format('yyyyMMdd')
      File overriddenTemplateFile = new File("templates/event_description_${eventId}.html")
      def template = Constants.templateEngine.createTemplate(overriddenTemplateFile.exists() ? overriddenTemplateFile : defaultTemplateFile)
      def binding = [event: event]
      new File("event_description_${eventId}.html").text = template.make(binding).toString()
    }
    futureEvents.each { Map event ->
      String eventId = dateFormat.parse(event.date as String).format('yyyyMMdd')

      // Find EventBrite event ID if it is not yet set or missing.
      String eventbriteEventId = event.eventbriteEventId
      if (!eventbriteEventId) {
        eventBrite.events.each { Map eventbriteEvent ->
          if (isoDateFormat.parse(eventbriteEvent.start.local).format('yyyyMMdd') == eventId) {
            eventbriteEventId = eventbriteEvent.id
          }
        }
      }

      // Calculate input parameters.
      String apiUrl = eventbriteEventId ? "/v3/events/${eventbriteEventId}/" : "/v3/events/"
      def startTime = isoDateFormat.parse(dateFormat.parse(event.date).format('yyyy-MM-dd') + 'T' + event.time + ':00')
      def endTime = isoDateFormat.parse(dateFormat.parse(event.date).format('yyyy-MM-dd') + 'T' + event.endTime + ':00')

      // Load overridden event data.
      File overriddenDataFile = new File("data/${eventId}.json")
      def overriddenData = [:]
      if (overriddenDataFile.exists()) {
        overriddenData = new JsonSlurper().parse(overriddenDataFile)
      }

      // Create or update event information.
      println "> Creating/updating \"LatCraft | ${event.theme}\" (${eventId}, ${eventbriteEventId})"
      eventBrite.post(apiUrl, [
        event: [
          name          : [
            html: "LatCraft | ${event.theme}".toString()
          ],
          currency      : 'EUR',
          start         : [
            utc     : startTime.format("yyyy-MM-dd'T'HH:mm:ss'Z'", gmt),
            timezone: 'Europe/Riga'
          ],
          end           : [
            utc     : endTime.format("yyyy-MM-dd'T'HH:mm:ss'Z'", gmt),
            timezone: 'Europe/Riga'
          ],
          venue_id      : overriddenData?.venue_id ?: latcraftEventbriteVenueId,
          organizer_id  : latcraftEventbriteOrganizerId,
          logo_id       : overriddenData?.logo_id ?: latcraftEventbriteLogoId,
          category_id   : overriddenData?.category_id ?: latcraftEventbriteCategoryId,
          subcategory_id: overriddenData?.subcategory_id ?: latcraftEventbriteSubcategoryId,
          format_id     : overriddenData?.format_id ?: latcraftEventbriteFormatId,
          capacity      : overriddenData?.capacity ?: latcraftEventbriteCapacity,
          show_remaining: true,
          description   : [
            html: temporaryFile("${buildDir}/event_description_${eventId}.html").text
          ]
        ]
      ]) { data ->
        if (!event.eventbriteEventId) {
          def events = getEventData()
          events.each { updatedEvent ->
            if (dateFormat.parse(updatedEvent.date).format('yyyyMMdd') == eventId) {
              updatedEvent.eventbriteEventId = data.id
              updatedEvent.tickets = data.url
              eventbriteEventId = data.id
            }
          }
          eventFile.text = dumpJson(events)
        }
      }

      // Retrieve existing ticket classes.
      apiUrl = "/v3/events/${eventbriteEventId}/ticket_classes/"
      String eventbriteTicketClassId = null
      eventBrite.get(apiUrl) { data ->
        eventbriteTicketClassId = data.ticket_classes?.find { true }?.id
      }

      // Create or update ticket class.
      apiUrl = eventbriteTicketClassId ? "/v3/events/${eventbriteEventId}/ticket_classes/${eventbriteTicketClassId}/" : "/v3/events/${eventbriteEventId}/ticket_classes/"
      eventBrite.post(apiUrl, [
        ticket_class: [
          name            : 'Free ticket',
          free            : true,
          minimum_quantity: 1,
          maximum_quantity: 1,
          // TODO: sales_end: event.time - 30 minutes,
          quantity_total  : latcraftEventbriteCapacity
        ]
      ]) { data ->
        log.debug data.toString()
      }

      // Publish event.
      // TODO: test if event is already published
      eventBrite.post("/v3/events/${eventbriteEventId}/publish/")

    }
    [:]
  }

  public static void main(String[] args) {
    new PublishEventOnEventBrite().execute([:], new InternalContext())
  }

}
