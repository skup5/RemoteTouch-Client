package security

import lib.node.Crypto
import lib.node.Encoding


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

    private val hashJs = Crypto.createHash(SHA_VERSION);

    override fun hash(text: String): String {
        hashJs.update(text, CHARSET);
        return hashJs.digest(ENCODING)
    }

    companion object {
        private const val SHA_VERSION = "sha256"
        private val ENCODING = Encoding.base64.name
        private val CHARSET = Encoding.utf8.name
    }

}