package cz.zelenikr.remotetouch.security;

import org.jetbrains.annotations.NotNull;

/**
 * Encoder / decoder to symmetric ciphering a small text data.
 * <p/>
 * {@code T} is type of used key.
 *
 * @author Roman Zelenik
 */
public interface SymmetricCipher<T> {

    /**
     * Changes current key by {@code newKey}. Returns true if cipher using the new key now.
     *
     * @param newKey the key that should replace the current key
     * @return true if key was successfully changed, false otherwise
     */
    boolean changeKey(@NotNull T newKey);

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
