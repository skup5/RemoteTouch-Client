package cz.zelenikr.remotetouch.security

import org.junit.Test
import kotlin.test.assertEquals

class SHAHashTest {

    @Test
    fun `hash old generated key`(){
        assertEquals("R7eG00h/U9wVRkiITVOpZWQJT9E1YZmTAIXBuRbNfz4=",SHAHash().hash("XA1[B@6799e8e"))
    }

    @Test
    fun `hash new generated key`(){
        assertEquals("KGuP8oQUhmTHg++1IYODoC8Aa9hSFZYUfimiEC03CdM=", SHAHash().hash("XA1g1JSX7uwb4gmUlmGEe4mFQ=="))
    }
}