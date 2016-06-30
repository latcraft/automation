package lv.latcraft.devternity.tickets

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import groovy.xml.XmlUtil
import org.apache.batik.transcoder.TranscoderInput
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.image.JPEGTranscoder

File svgTemplate = new File('badge_template.svg')
String baseName = "${indexString}_${lastName}"
File svgBadgeFile = file("${buildDir}/${baseName}_badge.svg")
File qrFile = renderQRCodeImage(getBadgeUrl(firstName, lastName, email, company), buildDir, baseName)
svgBadgeFile.text = modifySVG(svgTemplate.text, firstName, lastName, email, company, type, qrFile)
renderBadgeImage(svgBadgeFile, buildDir, baseName)
qrFile.delete()
svgBadgeFile.delete()


def modifySVG(String svgText, String firstName, String lastName, String email, String company, String badgeType, File qrFile) {
  def svg = new XmlSlurper().parseText(svgText)
  def badgeFirstName = svg.depthFirst().find { it.@id == 'badge-first-name' }
  def badgeLastName = svg.depthFirst().find { it.@id == 'badge-last-name' }
  badgeFirstName.replaceBody(sanitizeName(firstName))
  badgeLastName.replaceBody(sanitizeName(lastName))
  svg.depthFirst().find { it.@id == 'badge-company' }.replaceBody(sanitizeCompany(company))
  // svg.depthFirst().find { it.@id == 'badge-type' }.replaceBody badgeType
  svg.depthFirst().find {
    it.@id == 'badge-qr'
  }.@'xlink:href' = "data:image/png;base64,${qrFile.bytes.encodeBase64().toString().toList().collate(76)*.join().join(' ')}".toString()
  // def color = badgeType.startsWith('S') ? '225500' : badgeType.startsWith('O') ? '800000' : '162D50'
  // svg.depthFirst().find { it.@id == 'badge-color' }.@style = "fill:#${color};fill-opacity:1;fill-rule:evenodd;stroke:none".toString()
  XmlUtil.serialize(svg)
}


String sanitizeName(String name) {
  name.
    trim().
    split('\\s+').collect {
    it.
      trim().
      capitalize()
  }.
    join(' ')
}


String sanitizeCompany(String company) {
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


def renderBadgeImage(File svgFile, File baseDir, String baseName) {
  JPEGTranscoder t = new JPEGTranscoder()
  t.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, new Float(1))
  String svgURI = svgFile.toURL().toString()
  t.transcode(new TranscoderInput(svgURI), new TranscoderOutput(new FileOutputStream("${baseDir}/${baseName}_badge.jpg")))
}


File renderQRCodeImage(String content, baseDir, baseName, width = 300, height = 300) {
  File targetFile = new File("${baseDir}/${baseName}_qr.png")
  def hints = new EnumMap<EncodeHintType, Object>(EncodeHintType)
  hints.put(EncodeHintType.CHARACTER_SET, "UTF-8")
  hints.put(EncodeHintType.MARGIN, 0)
  BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints)
  MatrixToImageWriter.writeToPath(bitMatrix, "png", targetFile.toPath())
  targetFile
}


def getBadgeUrl(firstName, lastName, email, company) {
  "mailto:${email}"
}


def forEachPerson(Closure cl) {
  def lines = file(project.hasProperty('dataFile') ? project.dataFile : 'data.csv').readLines().drop(1).collect {
    it.trim()
  }.findAll { it.contains(';') }
  lines.each { String line ->
    def fields = line.split(';')
    cl(fields[0], fields[1], fields[2], fields[3], fields[3])
  }
}

