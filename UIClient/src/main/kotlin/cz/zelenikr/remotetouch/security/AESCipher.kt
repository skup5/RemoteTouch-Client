package cz.zelenikr.remotetouch.security

import cz.zelenikr.remotetouch.security.exception.UnsupportedCipherException
import java.nio.charset.StandardCharsets
import java.security.*
import java.util.*
import javax.crypto.*
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class AESCipher
/**
 * Initializes new AES cipher with a specific key.
 *
 * @param plainKey the given key like a plain text
 * @throws UnsupportedCipherException
 */
(plainKey: String) : SymmetricCipher<String> {

    private val cipher: Cipher
    private val initVector: IvParameterSpec
    private var secretKey: SecretKey


    init {
        secretKey = toSecretKey(plainKey)
        cipher = initCipher()
        initVector = toInitVector("encryptionIntVec")
    }

    override fun encrypt(plainText: String): String? {
        try {
            return String(Base64.getMimeEncoder().encode(encrypt(plainText.toByteArray(charset))), charset)
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        } catch (e: BadPaddingException) {
            e.printStackTrace()
        } catch (e: IllegalBlockSizeException) {
            e.printStackTrace()
        } catch (e: InvalidAlgorithmParameterException) {
            e.printStackTrace()
        }
        return null
    }

    override fun decrypt(encryptedText: String): String? {
        try {
            return String(decrypt(Base64.getMimeDecoder().decode(encryptedText.toByteArray(charset))), charset)
        } catch (e: BadPaddingException) {
            e.printStackTrace()
        } catch (e: IllegalBlockSizeException) {
            e.printStackTrace()
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        } catch (e: InvalidAlgorithmParameterException) {
            e.printStackTrace()
        }
        return null
    }

    override fun changeKey(newKey: String): Boolean {
        secretKey = toSecretKey(newKey)
        return true
    }

    @Throws(InvalidKeyException::class, BadPaddingException::class, IllegalBlockSizeException::class, InvalidAlgorithmParameterException::class)
    private fun encrypt(input: ByteArray): ByteArray {
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, initVector)
        return cipher.doFinal(input)
    }

    @Throws(InvalidKeyException::class, BadPaddingException::class, IllegalBlockSizeException::class, InvalidAlgorithmParameterException::class)
    private fun decrypt(input: ByteArray): ByteArray {
        cipher.init(Cipher.DECRYPT_MODE, secretKey, initVector)
        return cipher.doFinal(input)
    }

    companion object {
        private const val PLAIN_KEY_BYTE_LENGTH = 10
        private const val HASH_VERSION = "MD5"
        private val charset = StandardCharsets.UTF_8
        private const val ALGORITHM = "AES"
        private const val CIPHER = "AES/CBC/PKCS5Padding"

        /**
         * Generates new random key for AES cipher and returns it like a plain text.
         *
         * @return new random key
         */
        fun generatePlainAESKey(): String {
            return generatePassword(PLAIN_KEY_BYTE_LENGTH)
        }

        private fun generatePassword(length: Int,
                                     upperCase: Boolean = true, lowerCase: Boolean = true,
                                     numeric: Boolean = true,
                                     specialChar: Boolean = true): String {

            val availableChars = mutableListOf<Char>()
            if (upperCase) availableChars += ('A'..'Z')
            if (lowerCase) availableChars += ('a'..'z')
            if (numeric) availableChars += ('0'..'9')
            if (specialChar) availableChars += arrayOf('{', '}', '[', ']', '@', '#', '?', '!')

            val randomGenerator = SecureRandom()
            val password = CharArray(length)
            for (i in 0 until length) {
                password[i] = availableChars[randomGenerator.nextInt(availableChars.size)]
            }

            return String(password)
        }

        @Throws(UnsupportedCipherException::class)
        private fun initCipher(): Cipher {
            return try {
                Cipher.getInstance(CIPHER)
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
                throw UnsupportedCipherException(CIPHER)
            } catch (e: NoSuchPaddingException) {
                e.printStackTrace()
                throw UnsupportedCipherException(CIPHER)
            }
        }

        private fun toSecretKey(plainKey: String): SecretKey {
            val key = hashKey(plainKey.toByteArray(charset))
            return SecretKeySpec(key, ALGORITHM)
        }

        private fun toInitVector(iv: String): IvParameterSpec {
            return IvParameterSpec(iv.toByteArray(charset))
        }

        private fun hashKey(rawKey: ByteArray): ByteArray {
            return try {
                MessageDigest.getInstance(HASH_VERSION).digest(rawKey)
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
                ByteArray(0)
            }
        }


    }

}