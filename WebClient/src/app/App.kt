package app

import kotlinx.html.js.onClickFunction
import network.Client
import network.SocketIOClient
import react.*
import react.dom.button
import react.dom.h1

/**
 *
 * @author Roman Zelenik
 */
class App : RComponent<RProps, RState>() {

    val socket: Client

    init {
        val clientToken = "zelr"
        val serverUri = "localhost:8080/socket"
        val secureKey = ""
        socket = SocketIOClient(clientToken, serverUri, secureKey);
    }

    override fun RBuilder.render() {
        h1 { +"React Kotlin Template" }

        button {
            attrs {
                onClickFunction = {
                    if (socket.isConnected) {
                        socket.disconnect()
                    } else {
                        socket.connect()
                    }
                }
                +"Socket.IO"
            }
        }
    }
}

fun RBuilder.app(): ReactElement {
    return child(App::class) {}
}