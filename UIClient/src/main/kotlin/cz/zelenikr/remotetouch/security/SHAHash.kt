package cz.zelenikr.remotetouch.security

import java.nio.charset.Charset
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

/**
 * Simple class to make SHA hash of plain text. It is using Base64 MIME type encoder to encodes hash to string.
 *
 * @author Roman Zelenik
 */
class SHAHash (
        /**
         * Sets true to using standard line separator by MIME type base64 encoding schema.
         *
         * @param useLineSeparator default is true
         */
        var isUseLineSeparator: Boolean = true) : Hash {

    private val messageDigest: MessageDigest
    private val charset: Charset

    init {
        charset = Charset.forName(CHARSET)
        messageDigest = try {
            MessageDigest.getInstance(SHA_VERSION)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        }
    }

    override fun hash(text: String): String {
        val hash = hash(text.toByteArray(charset))
        val encoder = if (isUseLineSeparator) Base64.getMimeEncoder() else Base64.getMimeEncoder(0, ByteArray(0))
        return String(encoder.encode(hash), charset)
    }

    private fun hash(input: ByteArray): ByteArray {
        return messageDigest.digest(input)
    }

    companion object {
        private const val SHA_VERSION = "SHA-256"
        private const val CHARSET = "UTF-8"
    }
}