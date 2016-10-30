package lv.latcraft.event.tasks

import com.amazonaws.services.lambda.runtime.Context
import groovy.util.logging.Log4j
import lv.latcraft.event.lambda.InternalContext

@Log4j("logger")
class ListSendGridSuppressedEmails extends BaseTask {

  Map<String, String> doExecute(Map<String, String> request, Context context) {
    Map<String, String> response = [:]
    sendGrid.execute()
    response
  }

  public static void main(String[] args) {
    new ListSendGridSuppressedEmails().execute([:], new InternalContext())
  }

}
