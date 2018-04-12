package cz.zelenikr.remotetouch.security;

import com.sun.istack.internal.NotNull;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import cz.zelenikr.remotetouch.security.exception.UnsupportedCipherException;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public final class AESCipher implements SymmetricCipher<String> {

    private static final int KEY_BITS_LENGTH = 128; // 192 and 256 bits may not be available
    private static final String HASH_VERSION = "SHA-1";
    private static final Charset charset = StandardCharsets.UTF_8;

    private final Cipher cipher;
    private SecretKey secretKey;

    /**
     * Initializes new AES cipher with a specific key.
     *
     * @param plainKey the given key like a plain text
     * @throws UnsupportedCipherException
     */
    public AESCipher(@NotNull String plainKey)throws UnsupportedCipherException {
        this(toSecretKey(plainKey));
    }

    /**
     * @param secretKey
     * @throws UnsupportedCipherException
     */
    private AESCipher(SecretKey secretKey) throws UnsupportedCipherException {
        this.secretKey = secretKey;
        this.cipher = initCipher();
    }

    /**
     * Generates new random key for AES cipher and returns it.
     *
     * @return new random key or null if some error occurred
     */
    public static byte[] generateAESKey() {
        try {
            return generateKey().getEncoded();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Generates new random key for AES cipher and returns it like a plain text.
     *
     * @return new random key or null if some error occurred
     */
    public static String generatePlainAESKey() {
        try {
            return generateKey().getEncoded().toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Cipher initCipher() throws UnsupportedCipherException {
        try {
            return Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
            throw new UnsupportedCipherException("AES");
        }
    }

    private static SecretKey toSecretKey(String plainKey) {
        byte[] key = plainKey.getBytes(charset);
        key = hashKey(key);
        return new SecretKeySpec(key, "AES");
    }

    private static SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(KEY_BITS_LENGTH);
        return kgen.generateKey();
    }

    private static byte[] hashKey(byte[] rawKey) {
        MessageDigest sha = null;
        try {
            sha = MessageDigest.getInstance(HASH_VERSION);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] key = sha.digest(rawKey);
        key = Arrays.copyOf(key, 16); // use only first 128 bit
        return key;
    }

    @Override
    public String encrypt(String plainData) {
        try {
            return Base64.encode(encrypt(plainData.getBytes(charset)));
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String decrypt(String base64EncryptedMessage) {
        try {
            return new String(decrypt(Base64.decode(base64EncryptedMessage)), charset);
        } catch (Base64DecodingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean changeKey(@NotNull String plainKey){
        this.secretKey = toSecretKey(plainKey);
        return true;
    }

    private byte[] encrypt(byte[] input) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(input);
    }

    private byte[] decrypt(byte[] input) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(input);
    }

}
