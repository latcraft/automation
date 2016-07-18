package lv.latcraft.event

import groovy.text.SimpleTemplateEngine

import java.text.SimpleDateFormat

class Constants {

  static eventFile = file("${buildDir}/events.json")
  static templateEngine = new SimpleTemplateEngine()
  static timeZone = TimeZone.getTimeZone('Europe/Riga')
  static gmt = TimeZone.getTimeZone("GMT")
  static dateFormat = new SimpleDateFormat('d MMMM, yyyy')
  static isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

  static {
    dateFormat.timeZone = timeZone
    isoDateFormat.timeZone = timeZone
  }

}
