package cz.zelenikr.remotetouch.security.exception;

/**
 * @author Roman Zelenik
 */
public class UnsupportedCipherException extends RuntimeException {
    public UnsupportedCipherException(String message) {
        super(message);
    }

    public UnsupportedCipherException(Exception e) {
        super(e);
    }
}
