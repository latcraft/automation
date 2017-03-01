package lv.latcraft.event.utils

import static groovy.json.JsonOutput.prettyPrint
import static groovy.json.JsonOutput.toJson

class Utils {

  static String dumpJson(obj) {
    prettyPrint(toJson(obj))
  }

}


