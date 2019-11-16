package dao

import Video
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import service.fetch

/**
 * Allows asynchronously load videos.
 * @author Roman Zelenik
 */
object VideoDao {

    val url = "https://my-json-server.typicode.com/kotlin-hands-on/kotlinconf-json/videos"

    suspend fun get(id: Int): Video = fetch("$url/$id")

    suspend fun getMany(vararg ids: Int): List<Video> = coroutineScope {
        ids.map { id ->
            async {
                get(id)
            }
        }.awaitAll()
    }

    suspend fun getMany(ids: IntRange): List<Video> = coroutineScope {
        getMany(*ids.toList().toIntArray())
    }
}