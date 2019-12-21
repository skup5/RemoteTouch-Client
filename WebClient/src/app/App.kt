package app

import data.dto.event.NotificationEventContent
import kotlinx.html.js.onClickFunction
import lib.materialui.core.MButtonColor
import lib.materialui.core.mButton
import manager.ConnectionManager
import network.ConnectionStatus
import network.ContentReceivedListener
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
        state = AppState(ConnectionStatus.DISCONNECTED, mutableListOf())

    }

    override fun componentDidMount() {
        with(socket) {
            registerConnectionStateChangedListener { value: ConnectionStatus -> setState { connectionStatus = value } }

            registerNotificationReceivedListener(object : ContentReceivedListener<NotificationEventContent> {
                    override fun onReceived(vararg contents: NotificationEventContent) {
                        setState { notifications += contents }
                    }
                })
        }
//        socket.connect()
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

        mButton {
            attrs {
                variant = "contained"
                color = MButtonColor.primary.toString()

            }
            +"Socket.IO"
        }

        mButton {
            attrs {
                variant = "outlined"
                color = MButtonColor.primary.toString()

            }
            +"Socket.IO"
        }

        div {
            status {
                content = connectionStatus.name
            }
        }

        simpleList {
            items = state.notifications
        }
    }

}

data class AppState(var connectionStatus: ConnectionStatus, var notifications: List<NotificationEventContent>) : RState

fun RBuilder.app(): ReactElement {
    return child(App::class) {}
}