/*
 * Copyright (C) 2021 Alceatraz @ BlackTechStudio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the BTS Anti-Commercial & GNU Affero General.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * BTS Anti-Commercial & GNU Affero General Public License for more details.
 *
 * You should have received a copy of the BTS Anti-Commercial & GNU Affero
 * General Public License along with this program in README or LICENSE.
 */
package studio.blacktech.furryblack.core.enhance

import studio.blacktech.furryblack.core.enhance.Enhance.getBytes
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets.UTF_8
import java.security.MessageDigest
import java.util.Base64

object MessageDigest {

  private val sha256 by lazy { MessageDigest.getInstance("SHA-256") }
  private val sha384 by lazy { MessageDigest.getInstance("SHA-384") }
  private val sha512 by lazy { MessageDigest.getInstance("SHA-512") }

  private val base64 by lazy { Base64.getEncoder() }

  private fun ByteArray.base64() = base64.encode(this)

  private infix fun ByteArray.sha256(charset: Charset) = sha256.digest(this).base64().toString(charset)
  private infix fun ByteArray.sha384(charset: Charset) = sha384.digest(this).base64().toString(charset)
  private infix fun ByteArray.sha512(charset: Charset) = sha512.digest(this).base64().toString(charset)

  @JvmStatic fun sha256(message: String) = message getBytes UTF_8 sha256 UTF_8
  @JvmStatic fun sha384(message: String) = message getBytes UTF_8 sha384 UTF_8
  @JvmStatic fun sha512(message: String) = message getBytes UTF_8 sha512 UTF_8

}