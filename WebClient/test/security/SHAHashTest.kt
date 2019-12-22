package security

import kotlin.test.*

class SHAHashTest {

    @Test
    fun hash() {
        val expected = "R7eG00h/U9wVRkiITVOpZWQJT9E1YZmTAIXBuRbNfz4="
        val actual = SHAHash().hash("XA1"+"[B@6799e8e")
        assertEquals(expected, actual)
    }
}