package lv.latcraft.event

import static groovy.json.JsonOutput.prettyPrint
import static groovy.json.JsonOutput.toJson

class Utils {

  static String dumpJson(obj) {
    prettyPrint(toJson(obj))
  }

//
//
//  task getMasterData << {
//    buildDir.mkdirs()
//    eventFile.text = new URL(latcraftEventDataFile).text
//  }
//

}


