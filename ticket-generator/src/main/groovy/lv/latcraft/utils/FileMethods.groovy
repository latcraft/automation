package lv.latcraft.utils

import groovy.transform.CompileStatic
import groovy.transform.TypeChecked

@CompileStatic
@TypeChecked
class FileMethods {

  static file(String prefix, String suffix) {
    File.createTempFile(prefix, suffix)
  }

}
