package cz.zelenikr.remotetouch.security;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * @author Roman Zelenik
 */
public class SHAHash implements Hash {

    private static final String
        SHA_VERSION = "SHA-256",
        CHARSET = "UTF-8";

    private final MessageDigest messageDigest;
    private final Charset charset;

    public SHAHash() {
        charset = Charset.forName(CHARSET);
        try {
            messageDigest = MessageDigest.getInstance(SHA_VERSION);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String hash(String text) {
        byte[] hash = hash(text.getBytes(charset));
        return new String(Base64.getMimeEncoder().encode(hash), charset);
    }

    private byte[] hash(byte[] input) {
        return messageDigest.digest(input);
    }
}
