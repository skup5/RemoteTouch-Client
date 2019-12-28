package cz.zelenikr.remotetouch.security;

import cz.zelenikr.remotetouch.security.exception.UnsupportedCipherException;
import org.jetbrains.annotations.NotNull;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public final class AESCipher implements SymmetricCipher<String> {

    private static final int KEY_BITS_LENGTH = 128; // 192 and 256 bits may not be available
    private static final Charset charset = StandardCharsets.UTF_8;
    private static final String ALGORITHM = "AES";
    private static final String CIPHER = "AES/CBC/PKCS5Padding";

    private final Cipher cipher;
    private final IvParameterSpec initVector;
    private SecretKey secretKey;

    /**
     * Initializes new AES cipher with a specific key.
     *
     * @param plainKey the given key like a plain text
     * @throws UnsupportedCipherException
     */
    public AESCipher(@NotNull String plainKey) throws UnsupportedCipherException {
        this(toSecretKey(plainKey));
    }

    /**
     * @param secretKey
     * @throws UnsupportedCipherException
     */
    private AESCipher(SecretKey secretKey) throws UnsupportedCipherException {
        this.secretKey = secretKey;
        this.cipher = initCipher();
        this.initVector = toInitVector("encryptionIntVec");
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
        return Base64.getEncoder().encodeToString(generateAESKey());
    }

    private static Cipher initCipher() throws UnsupportedCipherException {
        try {
            return Cipher.getInstance(CIPHER);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
            throw new UnsupportedCipherException(CIPHER);
        }
    }

    private static SecretKey toSecretKey(String plainKey) {
        final byte[] key = Base64.getDecoder().decode(plainKey);
        return new SecretKeySpec(key, ALGORITHM);
    }

    private static IvParameterSpec toInitVector(String iv) {
        return new IvParameterSpec(iv.getBytes(charset));
    }

    private static SecretKey generateKey() throws NoSuchAlgorithmException {
        final KeyGenerator kgen = KeyGenerator.getInstance(ALGORITHM);
        kgen.init(KEY_BITS_LENGTH);
        return kgen.generateKey();
    }

    @Override
    public String encrypt(String plainData) {
        try {
//            return Base64.encode(encrypt(plainData.getBytes(charset)));
            return new String(Base64.getMimeEncoder().encode(encrypt(plainData.getBytes(charset))), charset);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String decrypt(String base64EncryptedMessage) {
        try {
            return new String(decrypt(Base64.getMimeDecoder().decode(base64EncryptedMessage.getBytes(charset))), charset);
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean changeKey(@NotNull String plainKey) {
        this.secretKey = toSecretKey(plainKey);
        return true;
    }

    private byte[] encrypt(byte[] input) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, initVector);
        return cipher.doFinal(input);
    }

    private byte[] decrypt(byte[] input) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        cipher.init(Cipher.DECRYPT_MODE, secretKey, initVector);
        return cipher.doFinal(input);
    }

}
