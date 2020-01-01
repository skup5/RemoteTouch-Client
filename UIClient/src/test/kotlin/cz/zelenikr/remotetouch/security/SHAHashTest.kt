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
        assertEquals("W3pj4LHfgr/269ifY/FgqENwVJKV4zfYuHThJVcxugo=", SHAHash().hash("XA1hO7nA1nNK4"))
    }
}