package lv.latcraft.event.utils

import static groovy.json.JsonOutput.prettyPrint
import static groovy.json.JsonOutput.toJson

class JsonMethods {

  static String dumpJson(obj) {
    prettyPrint(toJson(obj))
  }

}


