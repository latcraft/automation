package lv.latcraft.event.tasks

import com.amazonaws.services.lambda.runtime.Context
import lv.latcraft.event.lambda.InternalContext

class ListEventBriteVenues extends BaseTask {

  Map<String, String> execute(Map<String, String> input, Context context) {
    println "STEP 1: Received data: ${input}"
    Map<String, String> response = [:]
    eventBrite.events.each { Map event ->
      Map<String, ?> venue = eventBrite.getVenueData(event.venue_id as String)
      response.put(event.venue_id as String, "${venue.address.address_1}".toString())
    }
    response.each { key, value ->
      println "STEP 2: '${key}' -> ${value}"
    }
    response
  }

  public static void main(String[] args) {
    new ListEventBriteVenues().execute([:], new InternalContext())
  }

}
