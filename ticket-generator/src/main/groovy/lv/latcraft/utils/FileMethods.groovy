package lv.latcraft.utils

class FileMethods {

  static file(String prefix, String suffix) {
    File.createTempFile(prefix, suffix)
  }

}
