package security

import lib.node.Crypto

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

        val cipher = initCipher(secretKey)
        val encrypted = cipher.update(plainText, CHARSET, ENCODING);
        encrypted += cipher.final(ENCODING);
        return encrypted as? String
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

        val decipher = initDecipher(secretKey)
        // encryptedText is base64 encrypted message
        val decrypted = decipher.update(encryptedText, ENCODING, CHARSET);
        decrypted += decipher.final(CHARSET);
        return decrypted as? String
    }


    override fun changeKey(newKey: String): Boolean {
        this.secretKey = toSecretKey(newKey)
        return true
    }

    companion object {
        private const val KEY_BITS_LENGTH = 128 // 192 and 256 bits may not be available
        private const val IV_BITS_LENGTH = 16
        private const val ALGORITHM = "aes$KEY_BITS_LENGTH"

        private const val HASH_VERSION = "SHA-1"
        private const val CHARSET = "utf8"
        private const val ENCODING = "base64"


        private val buffer = kotlinext.js.require("buffer");


        /**
         * Generates new random key for AES cipher and returns it.
         *
         * @return new random key or null if some error occurred
         */
        /* fun generateAESKey(): ByteArray? {
             return try {
                 generateKey().getEncoded()
             } catch (e: NoSuchAlgorithmException) {
                 e.printStackTrace()
                 null
             }
         }*/

        /**
         * Generates new random key for AES cipher and returns it like a plain text.
         *
         * @return new random key or null if some error occurred
         */
        /* fun generatePlainAESKey(): String? {
             return try {
                 generateKey().getEncoded().toString()
             } catch (e: NoSuchAlgorithmException) {
                 e.printStackTrace()
                 null
             }
         }*/

        /**
         *
         */
        private fun initCipher(key: dynamic): dynamic {
            return Crypto.createCipheriv(ALGORITHM, key, generateIV())
        }

        private fun initDecipher(key: dynamic): dynamic {
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

        /*  private fun generateKey(): SecretKey {
              val kgen: KeyGenerator = KeyGenerator.getInstance("AES")
              kgen.init(KEY_BITS_LENGTH)
              return kgen.generateKey()
          }*/

        /*  private fun hashKey(rawKey: ByteArray): ByteArray {
              var sha: MessageDigest? = null
              try {
                  sha = MessageDigest.getInstance(HASH_VERSION)
              } catch (e: NoSuchAlgorithmException) {
                  e.printStackTrace()
              }
              var key: ByteArray = sha.digest(rawKey)
              key = Arrays.copyOf(key, 16) // use only first 128 bit
              return key
          }*/
    }

}

