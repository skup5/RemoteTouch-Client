package security

import lib.node.Buffer
import lib.node.BufferJS
import lib.node.Encoding

object Base64 {

    fun encode(src: ByteArray): ByteArray {
        val buffer = BufferJS.from(src)
        val string = buffer.toString(Encoding.base64.name)
        return ByteArray(string.length) { string[it].toByte() }
    }

    fun encode(src: String): String = BufferJS.from(src).toString(Encoding.base64.name)

    fun decode(src: String): Buffer = BufferJS.from(src, Encoding.base64)
}