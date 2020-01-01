package cz.zelenikr.remotetouch.security.exception

/**
 * @author Roman Zelenik
 */
class UnsupportedCipherException : RuntimeException {
    constructor(message: String?) : super(message) {}
    constructor(e: Exception?) : super(e) {}
}