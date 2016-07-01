package lv.latcraft.utils

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import org.apache.batik.transcoder.TranscoderInput
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.image.PNGTranscoder
import org.apache.fop.svg.PDFTranscoder

import static FileMethods.file

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

  static File renderPng(File svgFile) {
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
