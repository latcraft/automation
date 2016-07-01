package lv.latcraft.devternity.tickets

import com.amazonaws.services.lambda.runtime.Context
import groovy.util.slurpersupport.GPathResult
import groovy.xml.XmlUtil

import static lv.latcraft.utils.FileMethods.file
import static lv.latcraft.utils.SvgRenderingMethods.*
import static lv.latcraft.utils.XmlMethods.*

class TicketGenerator {

  static Map<String, String> generate(Map<String, String> data, Context context) {
    context.logger.log "Received data: ${data}"
    TicketInfo ticket = new TicketInfo(data)
    File svgFile = file('ticket', '.svg')
    byte[] qrPngData = renderQRCodeImage(getQRData(ticket))
    svgFile.text = prepareSVG(getSvgTemplate(), ticket, qrPngData)
    File qrFile = file('ticket-qr', '.png')
    qrFile.bytes = qrPngData
    File jpegFile = renderPng(svgFile)
    File pdfFile = renderPDF(svgFile)
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

  static String sanitizeName(String name) {
    name.
      trim().
      split('\\s+').
      collect { String part -> part.trim().capitalize() }.
      join(' ')
  }

  static String sanitizeCompany(String company) {
    sanitizeName(
      company.
        replaceAll('n/a', '').
        replaceAll('LV', '').
        replaceAll('Intelligent Technologies', 'IT').
        replaceAll('VSIA', '').
        replaceAll('vsia', '').
        replaceAll('sia', '').
        replaceAll('SIA', '').
        replaceAll('LTD', '').
        replaceAll('AS', '').
        replaceAll('ltd', '').
        replaceAll('Ltd.', '').
        replaceAll('Trade & Finance Group', '').
        replaceAll('Self Employed', '').
        replaceAll('GmbH', '').
        replaceAll('No Company :\\(', '').
        replaceAll('-', '').
        replaceAll('Latvia', '').
        replaceAll('private', '').
        trim().
        capitalize()
    )
  }

  static getQRData(TicketInfo ticket) {
    "mailto:${ticket.email}"
  }

}
