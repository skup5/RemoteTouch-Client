package cz.zelenikr.remotetouch.security

import org.junit.Before
import org.junit.Test
import java.util.logging.Logger
import kotlin.test.assertEquals

class AESCipherTest {

    private val logger =  Logger.getLogger(javaClass.simpleName)
    private lateinit var cipher: SymmetricCipher<String>

    @Before
    fun beforeTest() {
        val key ="g1JSX7uwb4gmUlmGEe4mFQ=="
        cipher = AESCipher(key)
    }

    @Test
    fun cipher() {
        val cipher = AESCipher(AESCipher.generatePlainAESKey())
        val plainText = "some plain text\nsecond plain text"
        val encrypted = cipher.encrypt(plainText)
        logger.severe("$plainText \n->")
        logger.severe(encrypted)
//        logger.debug(encrypted ?: "?")
        val decrypted = encrypted?.let { cipher.decrypt(it) } ?: "?"
        logger.severe(decrypted)
        assertEquals(plainText, decrypted)
    }

    @Test
    fun encrypt() {
        val plainText = """{"type":"sms", "content": {"number": 789456123, "text": "some plain text,${'\n'}some another text"}}"""
        val encrypted = cipher.encrypt(plainText)
        logger.severe("$plainText \n->")
        logger.severe(encrypted)
    }

    @Test
    fun decrypt() {
        val plainText = """{"type":"sms", "content": {"number": 789456123, "text": "some plain text,${'\n'}some another text"}}"""
        val encrypted = """+kIGBYEJeunQKELM16pIxoUvLmTOa+Ab+df8lAB3+5G9lPe1qdfItgr+AMf4SD9YCGFs1BYkYAZR
WIX5dJloWbx6Abpa/EtI+woHfnna6DEPtwPDn2KGfJxBwliz37ns"""
        val decrypted = cipher.decrypt(encrypted)
        assertEquals(plainText, decrypted)
    }
}