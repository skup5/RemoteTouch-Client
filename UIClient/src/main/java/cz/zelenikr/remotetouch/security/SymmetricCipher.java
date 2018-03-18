package cz.zelenikr.remotetouch.security;

import com.sun.istack.internal.NotNull;

/**
 * Encoder / decoder to symmetric ciphering a small text data.
 *
 * @author Roman Zelenik
 */
public interface SymmetricCipher {

    /**
     * Encrypts a specific plain text.
     *
     * @param plainText text to encryption
     * @return encrypted text or null on some error
     */
    String encrypt(@NotNull String plainText);

    /**
     * Decrypts a specific data which was encrypted by this SymmetricCipher.
     *
     * @param encryptedText encrypted text by this SymmetricCipher
     * @return decrypted text or null on some error
     */
    String decrypt(@NotNull String encryptedText);
}
