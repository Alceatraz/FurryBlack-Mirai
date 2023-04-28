package studio.blacktech.furryblack.core.enhance

import java.io.PrintWriter
import java.io.StringWriter
import java.nio.charset.Charset

object Enhance {

  infix fun String.getBytes(charset: Charset) = toByteArray(charset)

  @JvmStatic fun Any.hexHash() = Integer.toHexString(hashCode()).uppercase()

  @JvmStatic fun extractStackTrace(throwable: Throwable?) = throwable?.extractStackTraces() ?: ""

  private fun Throwable.extractStackTraces(): String {
    val stringWriter = StringWriter()
    val printWriter = PrintWriter(stringWriter)
    printStackTrace(printWriter)
    return stringWriter.toString()
  }

  @JvmStatic fun toHumanReadable(value: Long) =
    if (value > 1024 * 1024 * 1024) {
      (value / (1024 * 1024 * 1024F)).toString() + "GB"
    } else if (value > 1024 * 1024) {
      (value / (1024 * 1024F)).toString() + "MB"
    } else if (value > 1024) {
      (value / 1024F).toString() + "kB"
    } else {
      value.toString() + "B"
    }

}