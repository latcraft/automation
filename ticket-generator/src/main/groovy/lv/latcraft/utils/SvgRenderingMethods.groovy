package lv.latcraft.utils

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import org.apache.avalon.framework.configuration.Configuration
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder
import org.apache.avalon.framework.container.ContainerUtil
import org.apache.batik.transcoder.TranscoderInput
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.image.PNGTranscoder
import org.apache.fop.svg.PDFTranscoder


import static FileMethods.file
import static java.lang.Boolean.FALSE
import static org.apache.batik.transcoder.SVGAbstractTranscoder.KEY_PIXEL_UNIT_TO_MILLIMETER
import static org.apache.batik.transcoder.XMLAbstractTranscoder.KEY_XML_PARSER_VALIDATING
import static org.apache.fop.svg.AbstractFOPTranscoder.KEY_AUTO_FONTS
import static org.apache.fop.svg.AbstractFOPTranscoder.KEY_STROKE_TEXT

class SvgRenderingMethods {

  static byte[] renderQRCodeImage(String content, int width = 300, int height = 300) {
    ByteArrayOutputStream byteStream = new ByteArrayOutputStream()
    EnumMap hints = new EnumMap<EncodeHintType, Object>(EncodeHintType)
    hints.put(EncodeHintType.CHARACTER_SET, "UTF-8")
    hints.put(EncodeHintType.MARGIN, 0)
    BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints)
    MatrixToImageWriter.writeToStream(bitMatrix, "png", byteStream)
    byteStream.toByteArray()
  }

  static File renderPNG(File svgFile) {
    PNGTranscoder t = new PNGTranscoder()
    String svgURI = svgFile.toURI().toString()
    File pngFile = file('temporary', '.png')
    t.transcode(
      new TranscoderInput(svgURI),
      new TranscoderOutput(
        new FileOutputStream(pngFile)
      )
    )
    pngFile
  }

  static File renderPDF(File svgFile) {
    PDFTranscoder t = new PDFTranscoder()
    DefaultConfigurationBuilder cfgBuilder = new DefaultConfigurationBuilder()
    Configuration cfg = cfgBuilder.buildFromFile(new File("fonts/pdf-renderer-cfg.xml"))
    ContainerUtil.configure(t, cfg)
    int dpi = 300
    t.addTranscodingHint(KEY_PIXEL_UNIT_TO_MILLIMETER, new Float((float) (25.4 / dpi)))
    t.addTranscodingHint(KEY_XML_PARSER_VALIDATING, FALSE)
    t.addTranscodingHint(KEY_STROKE_TEXT, FALSE)
    t.addTranscodingHint(KEY_AUTO_FONTS, false)
    String svgURI = svgFile.toURI().toString()
    File pdfFile = file('temporary', '.pdf')
    t.transcode(
      new TranscoderInput(svgURI),
      new TranscoderOutput(
        new FileOutputStream(pdfFile)
      )
    )
    pdfFile
  }

}
