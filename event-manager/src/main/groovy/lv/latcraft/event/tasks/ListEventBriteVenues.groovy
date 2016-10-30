package lv.latcraft.event.tasks

import com.amazonaws.services.lambda.runtime.Context
import groovy.util.logging.Log4j
import lv.latcraft.event.lambda.InternalContext

@Log4j("logger")
class ListEventBriteVenues extends BaseTask {

  Map<String, String> doExecute(Map<String, String> request, Context context) {
    Map<String, String> response = [:]
    eventBrite.events.each { Map event ->
      Map<String, ?> venue = eventBrite.getVenueData(event.venue_id as String)
      response.put(event.venue_id as String, "${venue.name}, ${venue.address.address_1}".toString())
    }
    response.each { key, value ->
      logger.info "${key} -> ${value}"
    }
    slack.send("Master, here are available EventBrite venues: \n" + response.sort { entry -> entry.value }.collect { key, value -> "${key} -> ${value}" }.join("\n"))
    response
  }

  public static void main(String[] args) {
    new ListEventBriteVenues().execute([:], new InternalContext())
  }

}
