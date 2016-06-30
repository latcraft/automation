package lv.latcraft.devternity.tickets

import com.amazonaws.services.lambda.runtime.Context
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import groovy.util.slurpersupport.GPathResult
import groovy.xml.XmlUtil
import org.apache.batik.transcoder.TranscoderInput
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.image.JPEGTranscoder
import org.apache.fop.svg.PDFTranscoder

class TicketGenerator {

  static String generate(Map<String, String> data, Context context) {
    context.logger.log "Received data: $data"
    File svgFile = File.createTempFile('ticket', '.svg')
    svgFile.text = prepareSVG(
      getSvgTemplate(),
      data.name,
      data.email,
      data.company,
      data.type,
      renderQRCodeImage(
        getQRData(
          data.name,
          data.email,
          data.company
        )
      )
    )
    renderImage(svgFile)
    renderPDF(svgFile)
    svgFile.delete()
  }

  static String getSvgTemplate() {
    getClass().getResource('/devternity_ticket.svg').text
  }

  static String prepareSVG(String svgText, String name, String email, String company, String badgeType, File qrFile) {
    GPathResult svg = new XmlSlurper().parseText(svgText)
    setElementValue(svg, 'ticket-name', sanitizeName(name).toUpperCase())
    setElementValue(svg, 'ticket-company', sanitizeName(company))
    setAttributeValue(svg, 'ticket-qr', 'xlink:href', "data:image/png;base64,${qrFile.bytes.encodeBase64().toString().toList().collate(76)*.join('').join(' ')}".toString())
    XmlUtil.serialize(svg)
  }

  static setElementValue(GPathResult svg, String elementId, String value) {
    findElementById(svg, elementId)?.replaceBody(value)
  }

  static setAttributeValue(GPathResult svg, String elementId, String attributeId, String value) {
    findElementById(svg, elementId)?.@"${attributeId}" = value
  }

  static Object findElementById(GPathResult svg, String elementId) {
    svg.depthFirst().find { it.@id == elementId }
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

  static renderImage(File svgFile) {
    JPEGTranscoder t = new JPEGTranscoder()
    t.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, new Float(1))
    String svgURI = svgFile.toURI().toString()
    t.transcode(
      new TranscoderInput(svgURI),
      new TranscoderOutput(
        new FileOutputStream("ticket.jpg")
      )
    )
  }

  static renderPDF(File svgFile) {
    PDFTranscoder t = new PDFTranscoder()
    String svgURI = svgFile.toURI().toString()
    t.transcode(
      new TranscoderInput(svgURI),
      new TranscoderOutput(
        new FileOutputStream("ticket.pdf")
      )
    )
  }

  static File renderQRCodeImage(String content, int width = 300, int height = 300) {
    File targetFile = new File("ticket-qr.png")
    EnumMap hints = new EnumMap<EncodeHintType, Object>(EncodeHintType)
    hints.put(EncodeHintType.CHARACTER_SET, "UTF-8")
    hints.put(EncodeHintType.MARGIN, 0)
    BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints)
    MatrixToImageWriter.writeToPath(bitMatrix, "png", targetFile.toPath())
    targetFile
  }

  static getQRData(name, email, company) {
    "mailto:${email}"
  }

}
