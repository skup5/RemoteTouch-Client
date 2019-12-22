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

    fun createCipheriv(algorithm: String, key: Any, generateIV: Any?, options: JsObject? = null): dynamic {
        return crypto.createCipheriv(algorithm, key, generateIV, options)
    }

    fun createDecipheriv(algorithm: String, key: Any, generateIV: Any?, options: JsObject? = null): dynamic {
        return crypto.createDecipheriv(algorithm, key, generateIV, options)
    }

    fun scryptSync(password: String, salt: String, keylen: Int, options: JsObject? = null): dynamic {
        return crypto.scryptSync(password, salt, keylen, options)
    }

}