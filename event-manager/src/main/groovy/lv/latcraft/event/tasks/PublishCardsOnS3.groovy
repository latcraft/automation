package lv.latcraft.event.tasks

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.s3.model.PutObjectRequest
import groovy.util.logging.Log4j
import groovy.util.slurpersupport.GPathResult
import groovy.xml.XmlUtil
import org.apache.commons.lang.WordUtils

import static lv.latcraft.event.utils.FileMethods.temporaryFile
import static lv.latcraft.event.utils.S3Methods.anyoneWithTheLink
import static lv.latcraft.event.utils.S3Methods.s3
import static lv.latcraft.event.utils.SanitizationMethods.replaceLatvianLetters
import static lv.latcraft.event.utils.SvgMethods.renderPNG
import static lv.latcraft.event.utils.XmlMethods.setAttributeValue
import static lv.latcraft.event.utils.XmlMethods.setElementValue

@Log4j
class PublishCardsOnS3 extends BaseTask {

  static final String BUCKET_NAME = 'latcraft-images'

  static final List<String> EVENT_CARDS = [
    'normal_event_card_v1',
    'normal_event_card_v2',
    'workshop_event_card_v1',
    'workshop_event_card_v2',
    'workshop_facebook_card'
  ]

  static final List<String> SPEAKER_CARDS = [
    'speaker_card_v1'
  ]

  Map<String, String> execute(Map<String, String> input, Context context) {
    log.info "STEP 1: Received data: ${input}"
    futureEvents.each { Map<String, ?> event ->
      String eventId = calculateEventId(event)
      EVENT_CARDS.each { String templateId ->
        String filePrefix = "event-${templateId}-${eventId}"
        File cardFile = temporaryFile(filePrefix, '.svg')
        println "> Generating ${filePrefix}"
        cardFile.text = generateEventCard(getSvgTemplate(templateId), event)
        s3.putObject(putRequest("${filePrefix}.png", renderPNG(cardFile)))
        // TODO: https://s3-eu-west-1.amazonaws.com/latcraft-images/event-normal_event_card_v2-20160803.png
      }
      event.schedule.each { Map<String, ?> session ->
        if (session.type == 'speech') {
          String speakerId = replaceLatvianLetters(session.name as String).trim().toLowerCase().replaceAll('[ ]', '_')
          SPEAKER_CARDS.each { String templateId ->
            String filePrefix = "event-${templateId}-${eventId}-${speakerId}"
            File cardFile = temporaryFile(filePrefix, '.svg')
            println "> Generating ${filePrefix}"
            cardFile.text = generateSpeakerCard(getSvgTemplate(templateId), event, session)
            s3.putObject(putRequest("${filePrefix}.png", renderPNG(cardFile)))
          }
        }
      }
      // TODO: update data on github
      // TODO: notify slack
    }
    [:]
  }

  static String getSvgTemplate(String templateId) {
    String templateName = "${templateId}.svg"
    getClass().getResource("/cards/${templateName}")?.text ?: new File(templateName).text
  }

  static String generateEventCard(String svgTemplateText, Map<String, ?> event) {
    GPathResult svg = new XmlSlurper().parseText(svgTemplateText)
    setElementValue(svg, 'event-title', (event.'short-theme' ?: event.theme) as String)
    setElementValue(svg, 'event-time', event.time as String)
    setElementValue(svg, 'event-date', event.date as String)
    setElementValue(svg, 'event-location', event.venue as String)
    XmlUtil.serialize(svg)
  }

  static String generateSpeakerCard(String svgTemplateText, Map<String, ?> event, Map<String, ?> session) {
    GPathResult svg = new XmlSlurper().parseText(svgTemplateText)
    def titleLines = WordUtils.wrap(session.title.toString(), 35).readLines()
    setElementValue(svg, 'event-title', (event.'short-theme' ?: event.theme) as String)
    setElementValue(svg, 'speaker-name', replaceLatvianLetters(session.name as String))
    setElementValue(svg, 'session-title-line1', titleLines.first())
    setElementValue(svg, 'session-title-line2', titleLines.size() > 1 ? titleLines.get(1) : '')
    setElementValue(svg, 'event-time', session.time as String)
    setElementValue(svg, 'event-date', event.date as String)
    setElementValue(svg, 'event-location', event.venue as String)
    setAttributeValue(svg, 'speaker-image', 'xlink:href', "data:image/png;base64,${new URL("http://latcraft.lv/${session.img}").bytes.encodeBase64().toString().toList().collate(76)*.join('').join(' ')}".toString())
    XmlUtil.serialize(svg)
  }

  static PutObjectRequest putRequest(String targetFileName, File localFile) {
    new PutObjectRequest(
      BUCKET_NAME,
      targetFileName,
      localFile
    ).withAccessControlList(anyoneWithTheLink())
  }

}
