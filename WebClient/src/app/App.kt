package app

import kotlinx.html.js.onClickFunction
import manager.ConnectionManager
import network.ConnectionStatus
import react.*
import react.dom.button
import react.dom.div
import react.dom.h1
import view.simpleList
import view.status.status

/**
 *
 * @author Roman Zelenik
 */
class App : RComponent<RProps, AppState>() {

    private val socket = ConnectionManager

    init {
        state = AppState(ConnectionStatus.DISCONNECTED)

    }

    override fun componentDidMount() {
        socket.registerConnectionStateChangedListener { value: ConnectionStatus -> setState { connectionStatus = value } }
        socket.connect()
    }

    override fun RBuilder.render() {
        val connectionStatus = state.connectionStatus

        h1 { +"RemoteTouch" }

        button {
            attrs {
                onClickFunction = {
                    if (connectionStatus == ConnectionStatus.CONNECTED) {
                        socket.disconnect()
                    } else {
                        socket.connect()
                    }
                }
                +"""Socket.IO ${if (connectionStatus == ConnectionStatus.CONNECTED) "disconnect" else "connect"}"""
            }
        }

        div {
            status {
                content = connectionStatus.name
            }
        }

        simpleList {
            items = listOf()
        }
    }

}

data class AppState(var connectionStatus: ConnectionStatus) : RState

fun RBuilder.app(): ReactElement {
    return child(App::class) {}
}