package cz.zelenikr.remotetouch.security

/**
 * Encoder / decoder to symmetric ciphering a small text data.
 *
 *
 * `T` is type of used key.
 *
 * @author Roman Zelenik
 */
interface SymmetricCipher<T> {
    /**
     * Changes current key by `newKey`. Returns true if cipher using the new key now.
     *
     * @param newKey the key that should replace the current key
     * @return true if key was successfully changed, false otherwise
     */
    fun changeKey(newKey: T): Boolean

    /**
     * Encrypts a specific plain text.
     *
     * @param plainText text to encryption
     * @return encrypted text or null on some error
     */
    fun encrypt(plainText: String): String?

    /**
     * Decrypts a specific data which was encrypted by this SymmetricCipher.
     *
     * @param encryptedText encrypted text by this SymmetricCipher
     * @return decrypted text or null on some error
     */
    fun decrypt(encryptedText: String): String?
}