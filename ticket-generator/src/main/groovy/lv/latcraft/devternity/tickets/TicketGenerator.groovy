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

  static Map<String, String> generate(Map<String, String> data, Context context) {
    context.logger.log "Received data: ${data}"
    TicketInfo ticket = new TicketInfo(data)
    File svgFile = file('ticket', '.svg')
    byte[] qrPngData = renderQRCodeImage(getQRData(ticket))
    svgFile.text = prepareSVG(getSvgTemplate(), ticket, qrPngData)
    File qrFile = file('ticket-qr', '.png')
    qrFile.bytes = qrPngData
    File jpegFile = renderJpeg(svgFile)
    File pdfFile = renderPDF(svgFile)
    // TODO: upload to s3
    // TODO: update dynamoDb
    svgFile.delete()
    [
      status: 'OK'
    ]
  }

  static file(String prefix, String suffix) {
    File.createTempFile(prefix, suffix)
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

  static File renderJpeg(File svgFile) {
    JPEGTranscoder t = new JPEGTranscoder()
    t.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, new Float(1))
    String svgURI = svgFile.toURI().toString()
    File jpegFile = file('ticket', '.jpg')
    t.transcode(
      new TranscoderInput(svgURI),
      new TranscoderOutput(
        new FileOutputStream(jpegFile)
      )
    )
    jpegFile
  }

  static File renderPDF(File svgFile) {
    PDFTranscoder t = new PDFTranscoder()
    String svgURI = svgFile.toURI().toString()
    File pdfFile = file('ticket', '.pdf')
    t.transcode(
      new TranscoderInput(svgURI),
      new TranscoderOutput(
        new FileOutputStream(pdfFile)
      )
    )
    pdfFile
  }

  static byte[] renderQRCodeImage(String content, int width = 300, int height = 300) {
    ByteArrayOutputStream byteStream = new ByteArrayOutputStream()
    EnumMap hints = new EnumMap<EncodeHintType, Object>(EncodeHintType)
    hints.put(EncodeHintType.CHARACTER_SET, "UTF-8")
    hints.put(EncodeHintType.MARGIN, 0)
    BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints)
    MatrixToImageWriter.writeToStream(bitMatrix, "png", byteStream)
    byteStream.toByteArray()
  }

  static getQRData(TicketInfo ticket) {
    "mailto:${ticket.email}"
  }

}
