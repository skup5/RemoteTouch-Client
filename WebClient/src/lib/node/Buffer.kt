package lib.node

@JsModule("buffer")
private external val bufferModule: dynamic

/**
 * [nodejs/buffer](https://nodejs.org/api/buffer.html)
 */
external class Buffer {
    fun toString(encoding: String? = definedExternally, start: Int? = definedExternally, end: Int? = definedExternally): String
}

object BufferJS {
    fun from(string: String, encoding: Encoding? = null): Buffer = bufferModule.Buffer.from(string, encoding?.name)

    fun from(array: ByteArray): Buffer = bufferModule.Buffer.from(array)
}

/**
 * [nodejs/buffer/buffers and character encodings](https://nodejs.org/api/buffer.html#buffer_buffers_and_character_encodings)
 */
enum class Encoding {
    ascii,
    utf8,
    utf16le,
    ucs2,
    base64,
    latin1,
    binary,
    hex
}