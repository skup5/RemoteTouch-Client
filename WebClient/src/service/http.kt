package service

import kotlinx.coroutines.await
import kotlin.browser.window

/**
 *
 * @author Roman Zelenik
 */

suspend fun <R> fetch(url: String): R {
    val responsePromise = window.fetch(url)
    val response = responsePromise.await()
    val jsonPromise = response.json()
    val json = jsonPromise.await()
    return json.unsafeCast<R>()
}