package security


/**
 * Simple class to make SHA hash of plain text. It is using Node.js crypto module.
 *
 * @author Roman Zelenik
 */
class SHAHash(
        /**
         * Sets true to using standard line separator by MIME type base64 encoding schema.
         *
         * @param useLineSeparator
         */
        var useLineSeparator: Boolean = true
) : Hash {

    private val crypto = kotlinext.js.require("crypto");
    private val hashJs = crypto.createHash(SHA_VERSION);

    override fun hash(text: String): String {
//        val hash: ByteArray = hash(text.getBytes(charset))
//        val encoder: Base64.Encoder = if (useLineSeparator) Base64.getMimeEncoder() else Base64.getMimeEncoder(0, ByteArray(0))
//        return String(encoder.encode(hash), charset)

        hashJs.update(text, CHARSET);
        val hashedText = hashJs.digest(ENCODING)

        return hashedText
    }

    companion object {
        private const val SHA_VERSION = "sha256"
        private const val ENCODING = "base64"
        private const val CHARSET = "UTF-8"
    }

}