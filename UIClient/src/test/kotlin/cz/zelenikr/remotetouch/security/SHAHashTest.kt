package cz.zelenikr.remotetouch.security

import org.junit.Test
import kotlin.test.assertEquals

class SHAHashTest {

    @Test
    fun hash(){
        assertEquals("R7eG00h/U9wVRkiITVOpZWQJT9E1YZmTAIXBuRbNfz4=",SHAHash().hash("XA1[B@6799e8e"))
    }
}