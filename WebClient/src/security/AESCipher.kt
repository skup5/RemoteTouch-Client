package security

import lib.node.*

class AESCipher
/**
 * Initializes new AES cipher with a specific key.
 *
 * @param plainKey the given key like a plain text
 */
(plainKey: String) : SymmetricCipher<String> {

    private var secretKey: dynamic = null

    init {
        changeKey(plainKey)
    }

    override fun encrypt(plainText: String): String? {
        val cipherJs = initCipher(secretKey)
        var encrypted = cipherJs.update(plainText, CHARSET, ENCODING);
        encrypted += cipherJs.final(ENCODING);
        return encrypted
    }


    override fun decrypt(encryptedText: String): String? {
        val decipherJs = initDecipher(secretKey)
        // encryptedText is base64 encrypted message
        var decrypted = decipherJs.update(encryptedText, ENCODING, CHARSET);
        decrypted += decipherJs.final(CHARSET);
        return decrypted
    }


    override fun changeKey(newKey: String): Boolean {
        this.secretKey = toSecretKey(newKey)
        return true
    }

    companion object {
        private const val KEY_BITS_LENGTH = 128 // 192 and 256 bits may not be available
        private const val ALGORITHM = "aes-$KEY_BITS_LENGTH-cbc"

        private val CHARSET = Encoding.utf8.name
        private val ENCODING = Encoding.base64.name

        private fun initCipher(key: dynamic): Cipher {
            return Crypto.createCipheriv(ALGORITHM, key, generateIV())
        }

        private fun initDecipher(key: dynamic): Decipher {
            return Crypto.createDecipheriv(ALGORITHM, key, generateIV())
        }

        private fun generateIV(): String = "encryptionIntVec"

        private fun toSecretKey(plainKey: String): Buffer = Base64.decode(plainKey)
    }
}

