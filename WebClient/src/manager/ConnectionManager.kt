package manager

import data.dto.event.NotificationEventContent
import lib.js.Callback
import log.Logger
import network.Client
import network.ConnectionStatus
import network.ContentReceivedListener
import network.SocketIOClient


/**
 * Manages connection and communication with mobile device via {@link Client}.
 *
 * @author Roman Zelenik
 */
object ConnectionManager {

    private val logger: Logger = Logger.getLogger("ConnectionManager")
    private var client: Client? = null

    // ContentReceivedListener sets
//    private val callReceivedListeners: Set<ContentReceivedListener<CallEventContent>> = setOf()
    private val notificationReceivedListeners: MutableSet<ContentReceivedListener<NotificationEventContent>> = mutableSetOf()
//    private val smsReceivedListeners: Set<ContentReceivedListener<SmsEventContent>> = setOf()

    // Connection state changed listeners
    private val connectionStateChangedListeners: MutableSet<Callback<ConnectionStatus>> = mutableSetOf()

    /**
     * Connects to a device. Client is initialized just once when this method is called.
     * If client is already connected, nothing happen.
     */
    fun connect() {
        if (client == null) {
            client = initClient();
        }
        if (!client!!.isConnected) {
            client!!.connect();
        }
    }

    /**
     * Disconnects a device.
     */
    fun disconnect() {
        client?.disconnect();
        client = null;
    }

    /**
     * Disconnects and establishes new connection to a mobile device.
     */
    fun reconnect() {
        if (client != null) {
            disconnect();
        }
        connect();
    }

    fun isConnected(): Boolean = client?.isConnected ?: false


    fun registerConnectionStateChangedListener(listener: Callback<ConnectionStatus>) {
        connectionStateChangedListeners += listener
    }

    /*
            fun registerCallReceivedListener(@NotNull ContentRecivedListener<CallEventContent> listener) {
                callReceivedListeners.add(listener);
            }
    */
    fun registerNotificationReceivedListener(listener: ContentReceivedListener<NotificationEventContent>) {
        notificationReceivedListeners += listener
    }

    /*
            fun registerSmsReceivedListener(@NotNull ContentRecivedListener<SmsEventContent> listener) {
                smsReceivedListeners.add(listener);
            }
*/
    fun removeConnectionStateChangedListener(listener: Callback<ConnectionStatus>) {
        connectionStateChangedListeners -= listener
    }

    /*
                fun removeCallReceivedListener(@NotNull ContentRecivedListener<CallEventContent> listener) {
                    callReceivedListeners.remove(listener);
                }
        */
    fun removeNotificationReceivedListener(listener: ContentReceivedListener<NotificationEventContent>) {
        notificationReceivedListeners -= listener
    }

    /*
            fun removeSmsReceivedListener(@NotNull ContentRecivedListener<SmsEventContent> listener) {
                smsReceivedListeners.remove(listener);
            }
        */
    private fun initClient(): Client {
        return SocketIOClient(loadClientToken(), loadServerAddress(), loadPairKey()).also {
            //        client.setOnCallReceived(contents -> notifyCallReceived(contents));
//            it.setOnNotificationReceived{notifyNotificationReceived(contents)}

//        client.setOnSMSReceived(contents -> notifySmsReceived(contents));
            it.setOnConnectionStatusChanged { status ->
                notifyConnectionStateChanged(status)
            }
        }
    }


    private fun notifyConnectionStateChanged(status: ConnectionStatus) {
//        logger.debug("notifyConnectionStateChanged: " + status.name)
        connectionStateChangedListeners.forEach { callback -> callback(status) }
    }

    /*
            private void notifyCallReceived(CallEventContent[] contents) {
                callReceivedListeners.forEach(contentRecivedListener -> contentRecivedListener.onReceived(contents));
            }
       */
    private fun notifyNotificationReceived(contents: Array<NotificationEventContent>) {
        notificationReceivedListeners.forEach { contentReceivedListener -> contentReceivedListener.onReceived(*contents) };
    }

//        private void notifySmsReceived(SmsEventContent[] contents) {
//            smsReceivedListeners.forEach(contentRecivedListener -> contentRecivedListener.onReceived(contents));
//        }

    private fun loadPairKey(): String {
        return "[B@6799e8e"
//        return SETTINGS.getPairKey();
    }

    private fun loadServerAddress(): String {
        return "localhost:8080/socket"
//            return SETTINGS.getServerAddress().toURI();
    }

    private fun loadClientToken(): String {
        return createToken("", loadPairKey())
//        return createToken(SETTINGS.getDeviceName(), loadPairKey());
    }

    private fun createToken(device: String, pairKey: String): String {
        return "R7eG00h/U9wVRkiITVOpZWQJT9E1YZmTAIXBuRbNfz4="
//        Hash hash = new SHAHash();
//        return hash.hash(device + pairKey);
    }

}
