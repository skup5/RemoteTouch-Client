package cz.zelenikr.remotetouch.security;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Simple class to make SHA hash of plain text. It is using Base64 MIME type encoder to encodes hash to string.
 *
 * @author Roman Zelenik
 */
public class SHAHash implements Hash {

    private static final String
            SHA_VERSION = "SHA-256",
            CHARSET = "UTF-8";

    private final MessageDigest messageDigest;
    private final Charset charset;

    private boolean useLineSeparator;

    /**
     * Creates new instance and sets {@code useLineSeparator} to true.
     */
    public SHAHash() {
        this(true);
    }

    public SHAHash(boolean useLineSeparator) {
        this.useLineSeparator = useLineSeparator;
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
        Base64.Encoder encoder = useLineSeparator ? Base64.getMimeEncoder() : Base64.getMimeEncoder(0, new byte[0]);
        return new String(encoder.encode(hash), charset);
    }

    public boolean isUseLineSeparator() {
        return useLineSeparator;
    }

    /**
     * Sets true to using standard line separator by MIME type base64 encoding schema.
     *
     * @param useLineSeparator
     */
    public void setUseLineSeparator(boolean useLineSeparator) {
        this.useLineSeparator = useLineSeparator;
    }

    private byte[] hash(byte[] input) {
        return messageDigest.digest(input);
    }
}
