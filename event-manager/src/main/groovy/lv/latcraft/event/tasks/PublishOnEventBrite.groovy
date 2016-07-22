package lv.latcraft.event.tasks

class PublishOnEventBrite {

//  ext {
//
//    sha1 = MessageDigest.getInstance("SHA1")
//
//    defaultTemplateFile = file('templates/event_description.html')
//
//  }
//
//  task makeEventBriteDescription(dependsOn: getMasterData) << {
//    buildDir.mkdirs()
//    getEvents().each { event ->
//      String eventId = dateFormat.parse(event.date).format('yyyyMMdd')
//      File overriddenTemplateFile = file("templates/event_description_${eventId}.html")
//      def template = templateEngine.createTemplate(overriddenTemplateFile.exists() ? overriddenTemplateFile : defaultTemplateFile)
//      def binding = [ event: event ]
//      file("${buildDir}/event_description_${eventId}.html").text = template.make(binding).toString()
//    }
//  }
//
//  task synchronizeEventBriteData(dependsOn: [getMasterData, getEventBriteData, makeEventBriteDescription]) << {
//    getFutureEvents().each { event ->
//
//      // Calculate unique event ID used to distinguish this event from others in various data sources.
//      String eventId = dateFormat.parse(event.date).format('yyyyMMdd')
//
//      // Find EventBrite event ID if it is not yet set or missing.
//      String eventbriteEventId = event.eventbriteEventId
//      if (!eventbriteEventId) {
//        getEventBriteEvents().each { eventbriteEvent ->
//          if (isoDateFormat.parse(eventbriteEvent.start.local).format('yyyyMMdd') == eventId) {
//            eventbriteEventId = eventbriteEvent.id
//          }
//        }
//      }
//
//      // Calculate input parameters.
//      String apiUrl = eventbriteEventId ? "/v3/events/${eventbriteEventId}/" : "/v3/events/"
//      def startTime = isoDateFormat.parse(dateFormat.parse(event.date).format('yyyy-MM-dd') + 'T' + event.time + ':00')
//      def endTime = isoDateFormat.parse(dateFormat.parse(event.date).format('yyyy-MM-dd') + 'T' + event.endTime + ':00')
//
//      // Load overridden event data.
//      File overriddenDataFile = file("data/${eventId}.json")
//      def overriddenData = [:]
//      if (overriddenDataFile.exists()) {
//        overriddenData = new JsonSlurper().parse(overriddenDataFile)
//      }
//
//      // Create or update event information.
//      println "> Creating/updating \"LatCraft | ${event.theme}\" (${eventId}, ${eventbriteEventId})"
//      eventbrite(POST, apiUrl, [
//        event: [
//          name: [
//            html: "LatCraft | ${event.theme}".toString()
//          ],
//          currency: 'EUR',
//          start: [
//            utc: startTime.format("yyyy-MM-dd'T'HH:mm:ss'Z'", gmt),
//            timezone: 'Europe/Riga'
//          ],
//          end: [
//            utc: endTime.format("yyyy-MM-dd'T'HH:mm:ss'Z'", gmt),
//            timezone: 'Europe/Riga'
//          ],
//          venue_id: overriddenData?.venue_id ?: latcraftEventbriteVenueId,
//          organizer_id: latcraftEventbriteOrganizerId,
//          logo_id: overriddenData?.logo_id ?: latcraftEventbriteLogoId,
//          category_id: overriddenData?.category_id ?: latcraftEventbriteCategoryId,
//          subcategory_id: overriddenData?.subcategory_id ?: latcraftEventbriteSubcategoryId,
//          format_id: overriddenData?.format_id ?: latcraftEventbriteFormatId,
//          capacity: overriddenData?.capacity ?: latcraftEventbriteCapacity,
//          show_remaining: true,
//          description: [
//            html: file("${buildDir}/event_description_${eventId}.html").text
//          ]
//        ]
//      ], 1) { data ->
//        if (!event.eventbriteEventId) {
//          def events = getEvents()
//          events.each { updatedEvent ->
//            if (dateFormat.parse(updatedEvent.date).format('yyyyMMdd') == eventId) {
//              updatedEvent.eventbriteEventId = data.id
//              updatedEvent.tickets = data.url
//              eventbriteEventId = data.id
//            }
//          }
//          eventFile.text = dumpJson(events)
//        }
//      }
//
//      // Retrieve existing ticket classes.
//      apiUrl = "/v3/events/${eventbriteEventId}/ticket_classes/"
//      String eventbriteTicketClassId = null
//      eventbrite(GET, apiUrl, [:], 1) { data ->
//        eventbriteTicketClassId = data.ticket_classes?.find{ true }?.id
//      }
//
//      // Create or update ticket class.
//      apiUrl = eventbriteTicketClassId ? "/v3/events/${eventbriteEventId}/ticket_classes/${eventbriteTicketClassId}/" : "/v3/events/${eventbriteEventId}/ticket_classes/"
//      eventbrite(POST, apiUrl, [
//        ticket_class: [
//          name: 'Free ticket',
//          free: true,
//          minimum_quantity: 1,
//          maximum_quantity: 1,
//          // TODO: sales_end: event.time - 30 minutes,
//          quantity_total: latcraftEventbriteCapacity
//        ]
//      ], 1) { data ->
//        logger.debug data.toString()
//      }
//
//      // Publish event.
//      apiUrl = "/v3/events/${eventbriteEventId}/publish/"
//      eventbrite(POST, apiUrl, [:], 1) { data ->
//        logger.debug data.toString()
//      }
//
//    }
//  }
//
//  updateMasterData.mustRunAfter synchronizeEventBriteData
//
//  task build(dependsOn: [synchronizeEventBriteData, updateMasterData])

}
