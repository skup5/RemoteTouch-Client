package cz.zelenikr.remotetouch.security

/**
 * @author Roman Zelenik
 */
interface Hash {
    /**
     * Makes hash from the specific string.
     *
     * @param text the given text you want to hash
     * @return hash value like a plain text
     */
    fun hash(text: String): String
}