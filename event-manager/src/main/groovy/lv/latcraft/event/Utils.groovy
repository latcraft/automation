package lv.latcraft.event

import static groovy.json.JsonOutput.prettyPrint
import static groovy.json.JsonOutput.toJson

class Utils {

  static void dumpJson(obj) {
    prettyPrint(toJson(obj))
  }

//  getFutureEvents = {
//    new JsonSlurper().parse(eventFile).findAll { isFutureEvent(it) }
//  }
//
//  eventbriteFile = file("${buildDir}/eventbrite.json")
//
//  getEventBriteEvents = {
//    new JsonSlurper().parse(eventbriteFile).events
//  }
//
//  calculateEventId = { event ->
//    // Calculate unique event ID used to distinguish this event from others in various data sources.
//    String eventId = dateFormat.parse(event.date).format('yyyyMMdd')
//  }
//
//
//  task getMasterData << {
//    buildDir.mkdirs()
//    eventFile.text = new URL(latcraftEventDataFile).text
//  }
//
//
//  getEvents = {
//    new JsonSlurper().parse(eventFile)
//  }
//
//  isFutureEvent = {
//    dateFormat.parse(it.date) > new Date()
//  }

}


