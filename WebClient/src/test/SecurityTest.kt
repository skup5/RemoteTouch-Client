package test

import log.Logger
import security.AESCipher
import security.SHAHash
import security.SymmetricCipher

class SecurityTest {

    private val logger: Logger = Logger.getLogger("SecurityTest")

    private fun createCipher(): SymmetricCipher<String> {
        return AESCipher("hO7nA1nNK4")
    }

    fun hashOld() {
        logger.debug("R7eG00h/U9wVRkiITVOpZWQJT9E1YZmTAIXBuRbNfz4=")
        logger.debug(SHAHash().hash("XA1[B@6799e8e"))
    }

    fun hashNew() {
        logger.debug("W3pj4LHfgr/269ifY/FgqENwVJKV4zfYuHThJVcxugo=")
        logger.debug(SHAHash().hash("XA1hO7nA1nNK4"))
    }

    fun cipher() {
        val cipher = createCipher()
        val plainText = """{"type":"sms", "content": {"number": 789456123, "text": "some plain text,${'\n'}some another text"}}"""
        val encrypted = cipher.encrypt(plainText)
        logger.debug(encrypted ?: "?")
        val decrypted = encrypted?.let { cipher.decrypt(it) } ?: "?"
        logger.debug("${(plainText == decrypted)}: $decrypted")
    }

    fun decryptEvent() {
        val encryptedEvent = """IYQSZxvZx6CdSEoqvs+KEJrJYTxKJdv92LfAlt0ANSc3PGL+L+epD0JNQaRLReAUr2c3fss1O+CN
CitLZ0Rj2ro+p78IsCSsu9OqxDRL759H85CH7iTD0nkOEr+l2Zkn"""
        val cipher = createCipher()
        val decrypted = cipher.decrypt(encryptedEvent)
        logger.debug(decrypted ?: "?")

    }
}