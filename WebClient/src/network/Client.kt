package network

import data.dto.event.CallEventContent
import data.dto.event.NotificationEventContent
import data.dto.event.SmsEventContent

/**
 * Represents communication with mobile device.
 *
 * @author Roman Zelenik
 */
interface Client {

    val isConnected: Boolean

    /**
     * Connects client.
     */
    fun connect()

    /**
     * Disconnects client.
     */
    fun disconnect()

    fun setOnConnectionStatusChanged(listener: (ConnectionStatus) -> Unit)

    fun setOnCallReceived(listener: ContentReceivedListener<CallEventContent>)

    fun setOnNotificationReceived(listener: ContentReceivedListener<NotificationEventContent>)

    fun setOnSMSReceived(listener: ContentReceivedListener<SmsEventContent>)

}
