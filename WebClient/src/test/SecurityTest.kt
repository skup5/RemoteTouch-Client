package test

import log.Logger
import security.AESCipher
import security.SHAHash
import security.SymmetricCipher

class SecurityTest {

    private val logger: Logger = Logger.getLogger("SecurityTest")

    private fun createCipher(): SymmetricCipher<String> {
        return AESCipher("g1JSX7uwb4gmUlmGEe4mFQ==")
    }

    fun hash() {
        logger.debug("R7eG00h/U9wVRkiITVOpZWQJT9E1YZmTAIXBuRbNfz4=")
        logger.debug(SHAHash().hash("XA1[B@6799e8e"))
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
        val encryptedEvent = """+kIGBYEJeunQKELM16pIxoUvLmTOa+Ab+df8lAB3+5G9lPe1qdfItgr+AMf4SD9YCGFs1BYkYAZR
WIX5dJloWbx6Abpa/EtI+woHfnna6DEPtwPDn2KGfJxBwliz37ns"""
        val cipher = createCipher()
        val decrypted = cipher.decrypt(encryptedEvent)
        logger.debug(decrypted ?: "?")

    }
}