/*
 * Copyright (C) 2021 Alceatraz @ BlackTechStudio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms from the BTS Anti-Commercial & GNU Affero General
 * Public License as published by the Free Software Foundation, either
 * version 3 from the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty from
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * BTS Anti-Commercial & GNU Affero General Public License for more details.
 *
 * You should have received a copy from the BTS Anti-Commercial & GNU Affero
 * General Public License along with this program.
 *
 */
@file:Suppress("unused")

package studio.blacktech.furryblack.core.enhance

import studio.blacktech.furryblackplus.FurryBlack
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor

object TimeTool {

  private val DATETIME = pattern("yyyy-MM-dd HH:mm:ss")
  private val DATE = pattern("yyyy-MM-dd")
  private val TIME = pattern("HH:mm:ss")

  private fun Long.to() = Instant.ofEpochMilli(this)

  private infix fun TemporalAccessor.to(format: String): String = pattern(format).format(this)
  private infix fun TemporalAccessor.to(formatter: DateTimeFormatter): String = formatter.format(this)

  @JvmStatic fun pattern(pattern: String): DateTimeFormatter = pattern(pattern, ZoneId.systemDefault())
  @JvmStatic fun pattern(pattern: String, zone: ZoneId): DateTimeFormatter = DateTimeFormatter.ofPattern(pattern).withZone(zone)

  @JvmStatic fun date() = Instant.now() to DATE
  @JvmStatic fun date(epoch: Long) = epoch.to() to DATE
  @JvmStatic fun date(instant: Instant) = instant to DATE

  @JvmStatic fun time() = Instant.now() to TIME
  @JvmStatic fun time(epoch: Long) = epoch.to() to TIME
  @JvmStatic fun time(instant: Instant) = instant to TIME

  @JvmStatic fun datetime() = Instant.now() to DATETIME
  @JvmStatic fun datetime(epoch: Long) = epoch.to() to DATETIME
  @JvmStatic fun datetime(instant: Instant) = instant to DATETIME

  @JvmStatic fun format(pattern: String) = Instant.now() to pattern
  @JvmStatic fun format(pattern: String, epoch: Long) = epoch.to() to pattern
  @JvmStatic fun format(pattern: String, instant: Instant) = instant to pattern

  @JvmStatic fun format(formatter: DateTimeFormatter) = Instant.now() to formatter
  @JvmStatic fun format(formatter: DateTimeFormatter, epoch: Long) = epoch.to() to formatter
  @JvmStatic fun format(formatter: DateTimeFormatter, instant: Instant) = instant to formatter

  @JvmStatic fun timeToTomorrow() = timeToTomorrow(0, 0, 0, 0)

  @JvmStatic fun timeToTomorrow(hh: Int = 0, mm: Int = 0, ss: Int = 0, sss: Int = 0): Long {
    val now = LocalDateTime.now()
    val next = LocalDateTime.of(now.year, now.monthValue, now.dayOfMonth + 1, hh, mm, ss, sss)
    return next.toEpochSecond(FurryBlack.SYSTEM_OFFSET) * 1000
  }

  @Suppress("DuplicatedCode")
  @JvmStatic fun duration(time: Long): String {

    var ms = time

    val dd = ms / 86400000
    ms %= 86400000
    val hh = ms / 3600000
    ms %= 3600000
    val mm = ms / 60000
    ms %= 60000
    val ss = ms / 1000
    ms %= 1000

    val builder = StringBuilder()

    return if (dd > 0) {
      builder.append(dd)
      builder.append("-")
      builder.append(String.format("%02d", hh))
      builder.append(":")
      builder.append(String.format("%02d", mm))
      builder.append(":")
      builder.append(String.format("%02d", ss))
      builder.append(".")
      builder.append(String.format("%03d", ms))
      builder.toString()
    } else if (hh > 0) {
      builder.append(hh)
      builder.append(":")
      builder.append(String.format("%02d", mm))
      builder.append(":")
      builder.append(String.format("%02d", ss))
      builder.append(".")
      builder.append(String.format("%03d", ms))
      builder.toString()
    } else if (mm > 0) {
      builder.append("00:")
      builder.append(String.format("%02d", mm))
      builder.append(":")
      builder.append(String.format("%02d", ss))
      builder.append(".")
      builder.append(String.format("%03d", ms))
      builder.toString()
    } else if (ss > 0) {
      builder.append("00:00:")
      builder.append(String.format("%02d", ss))
      builder.append(".")
      builder.append(String.format("%03d", ms))
      builder.toString()
    } else if (ms > 0) {
      builder.append("0.")
      builder.append(String.format("%03d", ms))
      builder.toString()
    } else {
      "0.000"
    }
  }
}