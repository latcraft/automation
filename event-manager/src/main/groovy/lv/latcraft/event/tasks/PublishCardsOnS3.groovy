package lv.latcraft.event.tasks

import com.amazonaws.services.lambda.runtime.Context
import groovy.util.logging.Log4j
import groovy.util.slurpersupport.GPathResult
import groovy.xml.XmlUtil
import lv.latcraft.event.lambda.InternalContext
import lv.latcraft.event.utils.S3Methods
import org.apache.commons.lang.WordUtils

import static lv.latcraft.event.utils.FileMethods.temporaryFile
import static lv.latcraft.event.utils.S3Methods.*
import static lv.latcraft.event.utils.SanitizationMethods.replaceLatvianLetters
import static lv.latcraft.event.utils.SvgMethods.renderPNG
import static lv.latcraft.event.utils.XmlMethods.setAttributeValue
import static lv.latcraft.event.utils.XmlMethods.setElementValue

@Log4j("logger")
class PublishCardsOnS3 extends BaseTask {

  static final List<String> EVENT_CARDS = [
    'normal_event_card_v1',                // twitter, linkedin
    'normal_event_card_v2',
    'normal_event_card_v3',
    'normal_event_facebook_background_v1',
    'normal_event_facebook_background_v2',
    'workshop_event_card_v1',
    'workshop_event_card_v2',
    'workshop_facebook_background_v1',
  ]

  static final List<String> SPEAKER_CARDS = [
    'speaker_card_v1',
    'speaker_card_v2',
  ]

  Map<String, String> doExecute(Map<String, String> request, Context context) {
    Map<String, String> response = [:]
    futureEvents.each { Map<String, ?> event ->

      String eventId = calculateEventId(event)

      event['cards'] = [:]
      EVENT_CARDS.each { String templateId ->

        String filePrefix = "event-${templateId}-${eventId}"
        File cardFile = temporaryFile(filePrefix, '.svg')

        // Generate event card.
        logger.info "Generating ${filePrefix}"
        cardFile.text = generateEventCard(getSvgTemplate(templateId), event)
        s3.putObject(putRequest("${filePrefix}.png", renderPNG(cardFile)))

        // Save result S3 object URLs.
        response[filePrefix] = getObjectUrl("${filePrefix}.png")
        event['cards'][filePrefix] = getObjectUrl("${filePrefix}.png")

      }

      event.schedule.each { Map<String, ?> session ->
        if (session.type == 'speech') {

          String speakerId = replaceLatvianLetters(session.name as String).trim().toLowerCase().replaceAll('[ ]', '_')
          session['cards'] = [:]
          SPEAKER_CARDS.each { String templateId ->

            String filePrefix = "event-${templateId}-${eventId}-${speakerId}"
            File cardFile = temporaryFile(filePrefix, '.svg')

            // Generate event card.
            logger.info "Generating ${filePrefix}"
            cardFile.text = generateSpeakerCard(getSvgTemplate(templateId), event, session)
            s3.putObject(putRequest("${filePrefix}.png", renderPNG(cardFile)))

            // Save result S3 object URLs.
            response[filePrefix] = getObjectUrl("${filePrefix}.png")
            session['cards'][filePrefix] = getObjectUrl("${filePrefix}.png")

          }
        }
      }

      // Update master data on GitHub.
      List<Map<String, ?>> eventsToUpdate = events
      eventsToUpdate.eachWithIndex  { eventToUpdate, i ->
        if (calculateEventId(eventToUpdate) == eventId) {
          eventsToUpdate[i] = event
        }
      }
      updateMasterData(eventsToUpdate)

      slack.send("Good news, master! Event cards are uploaded to AWS S3!")
      response.sort { it.key }.each { key, value ->
        slack.send(value)
      }

    }
    response
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

  public static void main(String[] args) {
    new PublishCardsOnS3().execute([:], new InternalContext())
  }

}
