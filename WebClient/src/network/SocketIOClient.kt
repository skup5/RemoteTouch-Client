package network

import data.dto.event.CallEventContent
import data.dto.event.NotificationEventContent
import data.dto.event.SmsEventContent
import data.dto.message.MessageDTO
import kotlinx.html.currentTimeMillis
import lib.js.Callback
import lib.socketio.client.*
import log.Logger
import kotlin.js.Json


/**
 * Enables real-time bidirectional communication with mobile device via remote Socket.IO server.
 *
 * @author Roman Zelenik
 */
class SocketIOClient(private val clientToken: String, private val serverUri: String, secureKey: String) : Client {


    val socket: Socket
//    private val symmetricCipher: SymmetricCipher

    private var connectionStatusChangedListener: ((ConnectionStatus) -> Unit)? = null
    private var callContentReceivedListener: ContentReceivedListener<CallEventContent>? = null
    private var notificationContentReceivedListener: ContentReceivedListener<NotificationEventContent>? = null
    private var smsContentReceivedListener: ContentReceivedListener<SmsEventContent>? = null

    override val isConnected: Boolean
        get() = socket.connected

    init {
//        val options = IO.Options()
//        options.secure = true

        //      Code for server self-signed certificate
        /*     var okHttpClient: OkHttpClient? = null
             try {
                 okHttpClient = createSSLSupport()
             } catch (e: CertificateException) {
                 throw RuntimeException(e)
             } catch (e: KeyStoreException) {
                 throw RuntimeException(e)
             } catch (e: NoSuchAlgorithmException) {
                 throw RuntimeException(e)
             } catch (e: IOException) {
                 throw RuntimeException(e)
             } catch (e: KeyManagementException) {
                 throw RuntimeException(e)
             }

             options.callFactory = okHttpClient
             options.webSocketFactory = okHttpClient*/
//        this.socket = IO.socket(serverUri, options)
        this.socket = IO.socket(serverUri)
//        this.symmetricCipher = AESCipher(secureKey)

        initSocket()
    }

    override fun connect() {
        socket.connect()
    }

    override fun disconnect() {
        socket.disconnect()

        notifyConnectionStatusChanged(ConnectionStatus.DISCONNECTED)
    }

    override fun setOnConnectionStatusChanged(listener: Callback<ConnectionStatus>) {
        connectionStatusChangedListener = listener
    }

    override fun setOnCallReceived(listener: ContentReceivedListener<CallEventContent>) {
        callContentReceivedListener = listener
    }

    override fun setOnNotificationReceived(listener: ContentReceivedListener<NotificationEventContent>) {
        notificationContentReceivedListener = listener
    }

    override fun setOnSMSReceived(listener: ContentReceivedListener<SmsEventContent>) {
        smsContentReceivedListener = listener
    }

    /**
     * Initializes [Socket] listeners.
     */
    private fun initSocket() {
        socket.on(EVENT_CONNECTING) {
            clientLogger.debug("connecting to $serverUri")
            notifyConnectionStatusChanged(ConnectionStatus.CONNECTING)
        }.on(EVENT_CONNECT) {
            clientLogger.debug("connected to $serverUri")
            notifyConnectionStatusChanged(ConnectionStatus.CONNECTED)
            sendIntro()
        }.on(EVENT_CONNECT_ERROR) {
            clientLogger.debug("connection error")
            notifyConnectionStatusChanged(ConnectionStatus.CONNECT_ERROR)
        }.on(EVENT_DISCONNECT) {
            clientLogger.debug("disconnected from $serverUri")
            notifyConnectionStatusChanged(ConnectionStatus.DISCONNECTED)
        }.on(EVENT_RECONNECTING) {
            clientLogger.debug("reconnecting to $serverUri")
            notifyConnectionStatusChanged(ConnectionStatus.RECONNECTING)
        }.on(EVENT_RECONNECT) {
            clientLogger.debug("reconnected to $serverUri")
            notifyConnectionStatusChanged(ConnectionStatus.RECONNECTED)
        }.on(EVENT_RECONNECT_ERROR) {
            clientLogger.debug("reconnection error")
            notifyConnectionStatusChanged(ConnectionStatus.RECONNECT_ERROR)
        }.on(EVENT_EVENT) { data ->
            clientLogger.info("received 'event'")
            if (data != null) {
                //logFine(data.toString());
                onEventReceived(data)
            }
        }.on(EVENT_MESSAGE) { data ->
            clientLogger.info(JSON.stringify(data))
            onNotificationReceived(NotificationEventContent("Test app", "Test label", "Test title", data?.get("res").toString(), currentTimeMillis()))
        }
    }

    /**
     * Notifies registered listener of change of [ConnectionStatus].
     *
     * @param newStatus the current status value
     */
    private fun notifyConnectionStatusChanged(newStatus: ConnectionStatus) {
        connectionStatusChangedListener?.let { it(newStatus) }
    }

    /**
     * Notifies registered [ContentReceivedListener<CallEventContent&gt;][ContentReceivedListener] of new call events.
     *
     * @param content the new call events
     */
    private fun onCallReceived(vararg content: CallEventContent) {
        //        clientLogger.info("call: " + Arrays.toString(content));
        callContentReceivedListener?.onReceived(*content)
    }

    /**
     * Processes received 'event' type [MessageDTO] from server. The message is expected in [Json] format.
     *
     * @param data the received message like a JSON
     */
    private fun onEventReceived(data: Json) {
//        if (data is Json) {
        // Read content from message
        val jsonArray = parseMessageContent(data)

        // Process array items
//            for (jsonArrayItem in jsonArray) {
//
//                // We expect only JSON objects
//                if (jsonArrayItem !is JSONObject) {
//                    continue
//                }
//
//                // Try parse EventDTO object from current array item
//                val eventDTO = JsonMapper.eventFromJson(jsonArrayItem as JSONObject) ?: continue
//
//                // Select listener by EventType
//                when (eventDTO.getType()) {
//                    EventType.CALL -> onCallReceived(eventDTO.getContent() as CallEventContent)
//                    EventType.NOTIFICATION -> onNotificationReceived(eventDTO.getContent() as NotificationEventContent)
//                    EventType.SMS -> onSmsReceived(eventDTO.getContent() as SmsEventContent)
//                    else -> clientLogger.severe("Unknown EventType: " + eventDTO.getType())
//                }
//            }
        println(jsonArray)
//        }
    }

    /**
     * Notifies registered [ContentReceivedListener<NotificationEventContent&gt;][ContentReceivedListener] of new notifications.
     *
     * @param content the  new notifications
     */
    private fun onNotificationReceived(vararg content: NotificationEventContent) {
        //        clientLogger.info("notification: " + Arrays.toString(content));
        notificationContentReceivedListener?.onReceived(*content)
    }

    /**
     * Notifies registered [ContentReceivedListener<SmsEventContent&gt;][ContentReceivedListener] of new sms events.
     *
     * @param content the new sms events
     */
    private fun onSmsReceived(vararg content: SmsEventContent) {
        //        clientLogger.info("sms: " + Arrays.toString(content));
        if (smsContentReceivedListener != null) smsContentReceivedListener!!.onReceived(*content)
    }

    /**
     * Reads and decrypts 'content' in the specific [Json]. Returns decrypted 'content' like Json[].
     * If `jsonObject` doesn't contain 'content' or it's empty, this method returns empty [Array<>].
     *
     * @param jsonObject the given object to parsing
     * @return 'content' value of `jsonObject` like [Array<>]
     */
    private fun parseMessageContent(jsonObject: Json): Array<Json> {
        /*var contentArray = JSONArray()

        // JSON is empty or doesn't contain 'content'
        if (json.length() === 0 || !json.has("content")) {
            return contentArray
        }

        try {
            // Get 'content' (JSONArray) from JSON
            contentArray = json.optJSONArray("content")

            // Get encrypted text content
            var content = contentArray.optString(0)
            if (content.isEmpty()) {
                return JSONArray()
            }

            // Decrypt text content
            content = decryptMessage(content)

            // Parse decrypted text to JSONArray of JSON objects
            contentArray = JSONArray(content)
        } catch (e: RuntimeException) {
            e.printStackTrace()
        }*/

        return arrayOf(jsonObject)
    }

    /**
     * Sends special ("pair") message to the server.
     */
    private fun sendIntro() {
        val json = MessageDTO(clientToken)
        //clientLogger.info("sendIntro " + json.toString());
        socket.emit(EVENT_INTRO, json)
    }

//    private fun decryptMessage(encrypted: String): String {
//        return symmetricCipher.decrypt(encrypted)
//    }

    companion object {
        private val clientLogger = Logger.getLogger("SocketIOClient")
        private const val EVENT_EVENT = "event"
        private const val EVENT_INTRO = "intro"
    }
}
