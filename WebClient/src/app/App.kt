package app

import data.dto.event.NotificationEventContent
import kotlinext.js.jsObject
import kotlinx.html.js.onClickFunction
import lib.materialui.MIconColor
import lib.materialui.core.*
import lib.materialui.mMessageIcon
import lib.materialui.mNotificationsIcon
import lib.materialui.mPhoneIcon
import manager.ConnectionManager
import network.ConnectionStatus
import network.ContentReceivedListener
import react.*
import react.dom.button
import react.dom.col
import react.dom.div
import react.dom.h1
import test.SecurityTest
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
        // runTests()
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

        mBottomNavigation {
            attrs {
                showLabels = true
                className = "bg-primary"
                onChange = { event, newValue ->
                    println(JSON.stringify(event))
                    println(newValue.toString())
                }
            }
            mBottomNavigationAction {
                attrs {
                    label = "Notifications"
                    icon = mNotificationsIcon(jsObject { color=MIconColor.disabled.toString() })
                }
            }

            mBottomNavigationAction {
                attrs {
                    label = "Messages"
                    icon = mMessageIcon(jsObject{color=MIconColor.primary.toString()})
                }
            }
            mBottomNavigationAction {
                attrs {
                    label = "Calls"
                    icon = mPhoneIcon(jsObject { color=MIconColor.secondary.toString() })
                }
            }
        }
    }


    private fun runTests() {
        val securityTest = SecurityTest()
        securityTest.cipher()
        securityTest.decryptEvent()
    }

}

data class AppState(var connectionStatus: ConnectionStatus, var notifications: List<NotificationEventContent>) : RState

fun RBuilder.app(): ReactElement {
    return child(App::class) {}
}