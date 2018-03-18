package cz.zelenikr.remotetouch.security;


import com.sun.org.apache.xml.internal.security.utils.Base64;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
        return Base64.encode(hash, 1);
    }

    private byte[] hash(byte[] input) {
        return messageDigest.digest(input);
    }
}
