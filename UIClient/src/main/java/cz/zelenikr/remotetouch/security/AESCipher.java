package cz.zelenikr.remotetouch.security;

import cz.zelenikr.remotetouch.security.exception.UnsupportedCipherException;
import org.jetbrains.annotations.NotNull;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class AESCipher implements SymmetricCipher<String> {

    private static final int KEY_BITS_LENGTH = 128; // 192 and 256 bits may not be available
    private static final int PLAIN_KEY_BYTE_LENGTH = 10;
    private static final String HASH_VERSION = "MD5";
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
     * Generates new random key for AES cipher and returns it like a plain text.
     *
     * @return new random key or null if some error occurred
     */
    public static String generatePlainAESKey() {
        return generatePassword(PLAIN_KEY_BYTE_LENGTH);
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
        final byte[] key = hashKey(plainKey.getBytes(charset));
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

    private static byte[] hashKey(byte[] rawKey) {
        try {
            return MessageDigest.getInstance(HASH_VERSION).digest(rawKey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    private static String generatePassword(int count) {
        List<Character> list = Stream.concat(
                getRandomAlphabets(count, true),
                getRandomAlphabets(count, false)
        ).collect(Collectors.toList());
        list.addAll(getRandomNumbers(count).collect(Collectors.toList()));
        Collections.shuffle(list);
        return list.subList(0, count).stream().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
    }

    private static Stream<Character> getRandomAlphabets(int count, boolean upperCase) {
        int randomNumberOrigin = 97;
        int randomNumberBound = 123;

        if (upperCase) {
            randomNumberOrigin = 65;
            randomNumberBound = 91;
        }

        return new SecureRandom().ints(count, randomNumberOrigin, randomNumberBound).mapToObj(data -> (char) data);
    }

    private static Stream<Character> getRandomNumbers(int count) {
        return new SecureRandom().ints(count, 48, 58).mapToObj(data -> (char) data);
    }

    @Override
    public String encrypt(String plainData) {
        try {
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
