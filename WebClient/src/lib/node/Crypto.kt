package lib.node

import kotlinext.js.JsObject

object Crypto {

    private val crypto = kotlinext.js.require("crypto");

    init {
        if (jsTypeOf(crypto.scrypt as? Any) != "function" && jsTypeOf(crypto.scryptSync as? Any) != "function") {
            val scrypt = kotlinext.js.require("browserify-scrypt")
            crypto.scrypt = scrypt.scrypt
            crypto.scryptSync = scrypt.scryptSync
        }
    }

    fun createCipheriv(algorithm: String, key: Any, generateIV: Any?, options: JsObject? = null): Cipher =
            crypto.createCipheriv(algorithm, key, generateIV, options)

    fun createDecipheriv(algorithm: String, key: Any, generateIV: Any?, options: JsObject? = null): Decipher =
            crypto.createDecipheriv(algorithm, key, generateIV, options)

    fun pbkdf2Sync(password: Any, salt: String, iterations: Int, keylen: Int, digest: String): Buffer =
            crypto.pbkdf2Sync(password, salt, iterations, keylen, digest)

    fun scryptSync(password: Any, salt: String, keylen: Int, options: JsObject? = null): Buffer =
            crypto.scryptSync(password, salt, keylen, options)

    fun createHash(algorithm: String, options: JsObject? = null): Hash = crypto.createHash(algorithm, options)
}

/**
 * [nodejs/crypto.Cipher](https://nodejs.org/api/crypto.html#crypto_class_cipher)
 */
external class Cipher {
    fun final(outputEncoding: String? = definedExternally): String
    fun update(data: String, inputEncoding: String? = definedExternally, outputEncoding: String? = definedExternally): String
    fun setAutoPadding(autoPadding: Boolean)
}

/**
 * [nodejs/crypto.Decipher](https://nodejs.org/api/crypto.html#crypto_class_decipher)
 */
external class Decipher {
    fun final(outputEncoding: String? = definedExternally): String
    fun update(data: String, inputEncoding: String? = definedExternally, outputEncoding: String? = definedExternally): String
    fun setAutoPadding(autoPadding: Boolean)
}

/**
 * [nodejs/crypto.Hash](https://nodejs.org/api/crypto.html#crypto_class_hash)
 */
external class Hash {
    fun digest(): Buffer
    fun digest(inputEncoding: String?): String
    fun update(buffer: Buffer): Buffer
    fun update(data: String, inputEncoding: String?): String
}