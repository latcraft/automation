package lv.latcraft.event.tasks

import com.amazonaws.services.lambda.runtime.Context
import groovy.util.logging.Log4j
import lv.latcraft.event.integrations.Configuration
import lv.latcraft.event.lambda.InternalContext

import static lv.latcraft.event.utils.Constants.*

@Log4j("logger")
class PublishEventOnEventBrite extends BaseTask {

  Map<String, String> doExecute(Map<String, String> request, Context context) {
    Map response = [:]
    validateEventBriteData()
    futureEvents.each { Map event ->

      String eventId = calculateEventId(event)

      // Find EventBrite event ID if it is not yet set or missing.
      String eventbriteEventId = event.eventbriteEventId
      if (!eventbriteEventId) {
        eventBrite.events.each { Map eventbriteEvent ->
          if (isoDateFormat.parse(eventbriteEvent.start.local as String).format('yyyyMMdd') == eventId) {
            eventbriteEventId = eventbriteEvent.id
          }
        }
      }

      // Calculate input parameters.
      String apiUrl = eventbriteEventId ? "/v3/events/${eventbriteEventId}/" : "/v3/events/"
      def startTime = isoDateFormat.parse(dateFormat.parse(event.date as String).format('yyyy-MM-dd') + 'T' + event.time + ':00')
      def endTime = isoDateFormat.parse(dateFormat.parse(event.date as String).format('yyyy-MM-dd') + 'T' + event.endTime + ':00')

      // Create or update event information.
      logger.info "Creating/updating \"LatCraft | ${event.theme}\" (${eventId}, ${eventbriteEventId})"
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
          venue_id      : event.venueId ?: Configuration.eventbriteVenueId,
          capacity      : event.capacity ?: Configuration.eventbriteCapacity,
          organizer_id  : event.eventBriteOrganizerId ?: Configuration.eventbriteOrganizerId,
          logo_id       : event.eventBriteLogoId ?: Configuration.eventbriteLogoId,
          category_id   : event.eventBriteCategoryId ?: Configuration.eventbriteCategoryId,
          subcategory_id: event.eventBriteSubcategoryId ?: Configuration.eventbriteSubcategoryId,
          format_id     : event.eventBriteFormatId ?: Configuration.eventbriteFormatId,
          show_remaining: true,
          description   : [
            html: createHtmlDescription(event)
          ]
        ]
      ]) { data ->
        if (!event.eventbriteEventId) {
          // Save EventBrite event ID and tickets URL on GitHub if it is missing.
          List<Map<String, ?>> eventsToUpdate = events
          eventsToUpdate.each { updatedEvent ->
            if (calculateEventId(updatedEvent) == eventId) {
              updatedEvent.eventbriteEventId = data.id
              updatedEvent.tickets = data.url
              updatedEvent.announced = "true"
              eventbriteEventId = data.id
            }
          }
          updateMasterData(eventsToUpdate)
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
          sales_end       : startTime.format("yyyy-MM-dd'T'HH:mm:ss'Z'", gmt),
          quantity_total  : event.capacity ?: Configuration.eventbriteCapacity
        ]
      ]) { data ->
        logger.debug data.toString()
      }

      response['eventBriteEventId'] = eventbriteEventId

      // Publish event.
      String eventStatus = eventBrite.get("/v3/events/${eventbriteEventId}") { data ->
        data.status
      }
      logger.info "Event status: ${eventStatus}"
      if (eventStatus == 'draft') {
        eventBrite.post("/v3/events/${eventbriteEventId}/publish/")
      }

      slack.send("Congratulations, master! Event \"LatCraft | ${event.theme}\" (${eventId}, ${eventbriteEventId}) was published (or updated) on EventBrite!")

    }
    response
  }

  private static void validateEventBriteData() {
    List<String> eventbriteEventIds = events.collect { Map event -> event.eventbriteEventId }.findAll { it }
    if (eventbriteEventIds.size() != eventbriteEventIds.unique().size()) {
      throw new RuntimeException("Duplicate EventBrite ids found in data!")
    }
  }

  private static String createHtmlDescription(Map event) {
    String eventId = calculateEventId(event)
    String defaultTemplate = getRemoteFileContents(new URL("${Configuration.eventbriteTemplateBaseDir}/event_description.html"))
    String overriddenTemplate = getRemoteFileContents(new URL("${Configuration.eventbriteTemplateBaseDir}/event_description_${eventId}.html"))
    def template = templateEngine.createTemplate(overriddenTemplate ?: defaultTemplate)
    template.make([event: event]).toString()
  }

  public static void main(String[] args) {
    new PublishEventOnEventBrite().execute([:], new InternalContext())
  }

}
