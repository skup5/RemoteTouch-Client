package security

import lib.node.Cipher
import lib.node.Crypto
import lib.node.Decipher

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
        /*  try { //            return Base64.encode(encrypt(plainData.getBytes(charset)));
              return String(Base64.getMimeEncoder().encode(encrypt(plainData.getBytes(charset))), charset)
          } catch (e: InvalidKeyException) {
              e.printStackTrace()
          } catch (e: BadPaddingException) {
              e.printStackTrace()
          } catch (e: IllegalBlockSizeException) {
              e.printStackTrace()
          }
          return null */

        val cipherJs = initCipher(secretKey)
        var encrypted = cipherJs.update(plainText, CHARSET, ENCODING);
        encrypted += cipherJs.final(ENCODING);
        return encrypted
    }


    override fun decrypt(encryptedText: String): String? {
        /* try {
             return String(decrypt(Base64.getMimeDecoder().decode(encryptedText.getBytes(charset))), charset)
         } catch (e: BadPaddingException) {
             e.printStackTrace()
         } catch (e: IllegalBlockSizeException) {
             e.printStackTrace()
         } catch (e: InvalidKeyException) {
             e.printStackTrace()
         }
         return null */

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
        private const val IV_BITS_LENGTH = 16
        private const val ALGORITHM = "aes$KEY_BITS_LENGTH"

        private const val CHARSET = "utf8"
        private const val ENCODING = "base64"

        private val buffer = kotlinext.js.require("buffer");

        /**
         *
         */
        private fun initCipher(key: dynamic): Cipher {
            return Crypto.createCipheriv(ALGORITHM, key, generateIV())
        }

        private fun initDecipher(key: dynamic): Decipher {
            return Crypto.createDecipheriv(ALGORITHM, key, generateIV())
        }

        private fun generateIV(): dynamic {
            // Use `crypto.randomBytes` to generate a random iv instead of the static iv
            // shown here.
            return buffer.Buffer.alloc(IV_BITS_LENGTH, 0) // Initialization vector.
        }

        private fun toSecretKey(plainKey: String): dynamic {
            return Crypto.scryptSync(plainKey, "salt", IV_BITS_LENGTH)
        }
    }
}

