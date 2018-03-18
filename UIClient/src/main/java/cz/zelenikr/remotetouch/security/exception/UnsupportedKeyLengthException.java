package cz.zelenikr.remotetouch.security.exception;

/**
 * @author Roman Zelenik
 */
public class UnsupportedKeyLengthException extends RuntimeException {
    public UnsupportedKeyLengthException(String message) {
        super(message);
    }
}
