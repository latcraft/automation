package lv.latcraft.devternity.tickets

import com.amazonaws.services.lambda.runtime.Context
import groovy.util.slurpersupport.GPathResult
import groovy.xml.XmlUtil

import static lv.latcraft.utils.FileMethods.file
import static lv.latcraft.utils.QRMethods.renderQRCodeImage
import static lv.latcraft.utils.SanitizationMethods.sanitizeCompany
import static lv.latcraft.utils.SanitizationMethods.sanitizeName
import static lv.latcraft.utils.SvgMethods.renderPDF
import static lv.latcraft.utils.XmlMethods.setAttributeValue
import static lv.latcraft.utils.XmlMethods.setElementValue

class TicketGenerator {

  static Map<String, String> generate(Map<String, String> data, Context context) {
    context.logger.log "STEP 1: Received data: ${data}"
    TicketInfo ticket = new TicketInfo(data)
    File svgFile = file('ticket', '.svg')
    byte[] qrPngData = renderQRCodeImage(getQRData(ticket))
    context.logger.log "STEP 2: Generated QR image"
    File qrFile = file('ticket-qr', '.png')
    qrFile.bytes = qrPngData
    context.logger.log "STEP 3: Saved QR image"
    svgFile.text = prepareSVG(getSvgTemplate(), ticket, qrPngData)
    context.logger.log "STEP 4: Pre-processed SVG template"
    File pdfFile = renderPDF(svgFile)
    context.logger.log "STEP 5: Generated PDF ticket"
    // TODO: upload to s3
    // TODO: update dynamoDb
    svgFile.delete()
    [
      status: 'OK'
    ]
  }

  static String getSvgTemplate() {
    getClass().getResource('/devternity_ticket.svg')?.text ?: new File('devternity_ticket.svg').text
  }

  static String prepareSVG(String svgText, TicketInfo ticket, byte[] qrImage) {
    GPathResult svg = new XmlSlurper().parseText(svgText)
    setElementValue(svg, 'ticket-name', sanitizeName(ticket.name).toUpperCase())
    setElementValue(svg, 'ticket-company', sanitizeCompany(ticket.company))
    setAttributeValue(svg, 'ticket-qr', 'xlink:href', "data:image/png;base64,${qrImage.encodeBase64().toString().toList().collate(76)*.join('').join(' ')}".toString())
    XmlUtil.serialize(svg)
  }

  static getQRData(TicketInfo ticket) {
    "mailto:${ticket.email}"
  }

}
