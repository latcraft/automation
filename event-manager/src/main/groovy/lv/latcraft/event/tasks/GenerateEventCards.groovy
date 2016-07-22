package lv.latcraft.event.tasks

import groovy.xml.XmlUtil
import org.apache.batik.transcoder.TranscoderInput
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.image.PNGTranscoder

class GenerateEventCards {

//
//  ['normal_event_card_v1', 'normal_event_card_v2', 'workshop_event_card_v1', 'workshop_event_card_v2', 'workshop_facebook_card'].each { templateId ->
//    File svgTemplate = file("templates/${templateId}.svg")
//    getEvents().each { event ->
//
//      String eventId = calculateEventId(event)
//      logger.quiet "> Generating '${templateId}' for ${event.theme} (${eventId})"
//      File svgFile = file("${buildDir}/${eventId}.svg")
//      svgFile.text = replaceTextInSVG(
//        svgTemplate.text,
//        [
//          'event-title':    event.'short-theme' ?: event.theme,
//          'event-time':     event.time,
//          'event-date':     event.date,
//          'event-location': event.venue,
//        ]
//      )
//      renderImage(
//        svgFile,
//        file("${buildDir}/${templateId}"),
//        eventId
//      )
//      svgFile.delete()
//
//    }
//  }
//}
//
//generateEventCards.logging.captureStandardOutput LogLevel.INFO
//generateEventCards.logging.captureStandardError LogLevel.INFO
//generateEventCards.outputs.dir file("${buildDir}")
//
//
//task generateSpeakerCards(dependsOn: getMasterData) << {
//  buildDir.mkdirs()
//  ['speaker_card'].each { templateId ->
//    File svgTemplate = file("templates/${templateId}.svg")
//    getEvents().each { event ->
//
//      String eventId = calculateEventId(event)
//      event.schedule.each { session ->
//        if (session.type == 'speech') {
//
//          String lvChars      = 'āēūīķņčžš'
//          String replaceChars = 'aeuiknczs'
//
//          String speakerId    = session.name.toLowerCase().trim().replaceAll('[\\s-]+', '_').tr(lvChars, replaceChars)
//
//          logger.quiet "> Generating '${templateId}' for ${event.theme} (${eventId} / ${speakerId})"
//
//          File svgFile        = file("${buildDir}/${eventId}.svg")
//          def titleLines      = WordUtils.wrap(session.title, 35).readLines()
//          svgFile.text        = replaceTextInSVG(
//            svgTemplate.text,
//            [
//              'event-title':          event.'short-theme' ?: event.theme,
//              'speaker-name':         session.name.tr(lvChars, replaceChars),
//              'session-title-line1':  titleLines.first(),
//              'session-title-line2':  titleLines.size() > 1 ? titleLines.get(1) : '',
//              'event-time':           session.time,
//              'event-date':           event.date,
//              'event-location':       event.venue,
//            ]
//          )
//          svgFile.text        = replaceImageInSVG(
//            svgFile.text,
//            'speaker-image',
//            "http://latcraft.lv/${session.img}"
//          )
//
//          renderImage(
//            svgFile,
//            file("${buildDir}/${templateId}"),
//            "${eventId}_${speakerId}"
//          )
//
//          svgFile.delete()
//
//        }
//      }
//    }
//  }
//}
//
//generateSpeakerCards.logging.captureStandardOutput LogLevel.INFO
//generateSpeakerCards.logging.captureStandardError LogLevel.INFO
//generateSpeakerCards.outputs.dir file("${buildDir}")
//
//
//def wrapLines(String line) {
//  line.each {
//
//  }
//}
//
  static replaceTextInSVG(String svgText, Map binding) {
    def svg = new XmlSlurper().parseText(svgText)
    binding.each { key, value ->
      def element = svg.depthFirst().find { it.@id == key }
      if (element) {
        element.replaceBody(value)
      }
    }
    XmlUtil.serialize(svg)
  }

  static replaceImageInSVG(String svgText, String elementId, String url) {
    def svg = new XmlSlurper().parseText(svgText)
    def element = svg.depthFirst().find { it.@id == elementId }
    if (element) {
      element.@'xlink:href' = "data:image/png;base64,${new URL(url).bytes.encodeBase64().toString().toList().collate(76)*.join().join(' ')}".toString()
    }
    XmlUtil.serialize(svg)
  }

  static renderImage(File svgFile, File baseDir, String baseName) {
    baseDir.mkdirs()
    PNGTranscoder t = new PNGTranscoder()
    String svgURI = svgFile.toURL().toString()
    t.transcode(new TranscoderInput(svgURI), new TranscoderOutput(new FileOutputStream("${baseDir}/${baseName}.png")))
  }

//// TODO: implement task for updating GitHub data and adding new/missing images

}
