package io.taskmonk.utils

import java.io.{ByteArrayOutputStream, File}
import java.nio.file.Files
import java.util.Base64
import java.util.zip.GZIPOutputStream

object FileUploadUtils {
  def getUploadContent(file: File): String = {
    val bytes = Files.readAllBytes(file.toPath)
    println("bytes = " + bytes.size)
    val arrOutputStream = new ByteArrayOutputStream()
    val zipOutputStream = new GZIPOutputStream(arrOutputStream)
    zipOutputStream.write(bytes)
    zipOutputStream.close()
    arrOutputStream.close()
    val output = arrOutputStream.toByteArray
    println("output = " + output.size)
    val encoded = Base64.getEncoder.encodeToString(output)
    println("encoded = " + encoded.size)
    encoded
  }
}
