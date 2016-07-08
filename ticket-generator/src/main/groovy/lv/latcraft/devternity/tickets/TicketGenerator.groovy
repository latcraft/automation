package lv.latcraft.devternity.tickets

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.s3.AmazonS3Client
import groovy.util.logging.Commons
import groovy.util.slurpersupport.GPathResult
import groovy.xml.XmlUtil

import static lv.latcraft.utils.FileMethods.file
import static lv.latcraft.utils.QRMethods.renderQRCodePNGImage
import static lv.latcraft.utils.SanitizationMethods.sanitizeCompany
import static lv.latcraft.utils.SanitizationMethods.sanitizeName
import static lv.latcraft.utils.SvgMethods.renderPDF
import static lv.latcraft.utils.XmlMethods.setAttributeValue
import static lv.latcraft.utils.XmlMethods.setElementValue

@Commons
class TicketGenerator {

  static Map<String, String> generate(Map<String, String> data, Context context) {
    log.info "STEP 1: Received data: ${data}"
    TicketInfo ticket = new TicketInfo(data)
    File svgFile = file('ticket', '.svg')
    byte[] qrPngData = renderQRCodePNGImage(getQRData(ticket))
    log.info "STEP 2: Generated QR image"
    File qrFile = file('ticket-qr', '.png')
    qrFile.bytes = qrPngData
    log.info "STEP 3: Saved QR image"
    def qrResult = s3.putObject('latcraft.images', "ticket-${ticket.ticketId}.png", qrFile)
    log.info "STEP 4: Uploaded PDF ticket"
    svgFile.text = prepareSVG(getSvgTemplate(ticket.product), ticket, qrPngData)
    log.info "STEP 5: Pre-processed SVG template"
    File pdfFile = renderPDF(svgFile)
    log.info "STEP 6: Generated PDF ticket"
    def pdfResult = s3.putObject('latcraft.images', "ticket-${ticket.ticketId}.pdf", pdfFile)
    log.info "STEP 7: Uploaded PDF ticket"
    // TODO: generate s3 urls
    svgFile.delete()
    [
      status: 'OK',
      qr: "${qrResult}",
      pdf: "${pdfResult}"
    ]
  }

  static String getSvgTemplate(String product) {
    String templateName = "DEVTERNITY_TICKET_${product}.svg"
    getClass().getResource("/${templateName}")?.text ?: new File(templateName).text
  }

  static AmazonS3Client getS3() {
    new AmazonS3Client()
  }

  static String prepareSVG(String svgText, TicketInfo ticket, byte[] qrImage) {
    GPathResult svg = new XmlSlurper().parseText(svgText)
    setElementValue(svg, 'ticket-name', sanitizeName(ticket.name).toUpperCase())
    setElementValue(svg, 'ticket-company', sanitizeCompany(ticket.company))
    setAttributeValue(svg, 'ticket-qr', 'xlink:href', "data:image/png;base64,${qrImage.encodeBase64().toString().toList().collate(76)*.join('').join(' ')}".toString())
    XmlUtil.serialize(svg)
  }

  static getQRData(TicketInfo ticket) {
    "${ticket.ticketId}"
  }

}
