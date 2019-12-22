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

    fun createCipheriv(algorithm: String, key: Any, generateIV: Any?, options: JsObject? = null): Cipher {
        return crypto.createCipheriv(algorithm, key, generateIV, options)
    }

    fun createDecipheriv(algorithm: String, key: Any, generateIV: Any?, options: JsObject? = null): Decipher {
        return crypto.createDecipheriv(algorithm, key, generateIV, options)
    }

    fun scryptSync(password: String, salt: String, keylen: Int, options: JsObject? = null): dynamic {
        return crypto.scryptSync(password, salt, keylen, options)
    }

    fun createHash(algorithm: String, options: JsObject? = null): Hash {
        return crypto.createHash(algorithm, options)
    }
}

/**
 * [nodejs/crypto.Cipher](https://nodejs.org/api/crypto.html#crypto_class_cipher)
 */
external class Cipher {
    fun final(outputEncoding: String? = definedExternally): String
    fun update(data: String, inputEncoding: String? = definedExternally, outputEncoding: String? = definedExternally): String
}

/**
 * [nodejs/crypto.Decipher](https://nodejs.org/api/crypto.html#crypto_class_decipher)
 */
external class Decipher {
    fun final(outputEncoding: String? = definedExternally): String
    fun update(data: String, inputEncoding: String? = definedExternally, outputEncoding: String? = definedExternally): String
}

/**
 * [nodejs/crypto.Hash](https://nodejs.org/api/crypto.html#crypto_class_hash)
 */
external class Hash {
    fun digest(inputEncoding: String? = definedExternally): String
    fun update(data: String, inputEncoding: String? = definedExternally)
}