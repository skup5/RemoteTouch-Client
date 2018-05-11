package cz.zelenikr.remotetouch.network;

import cz.zelenikr.remotetouch.Main;
import cz.zelenikr.remotetouch.Utils;
import cz.zelenikr.remotetouch.data.mapper.JsonMapper;
import cz.zelenikr.remotetouch.data.dto.event.*;
import cz.zelenikr.remotetouch.data.dto.message.MessageDTO;
import io.socket.client.IO;
import io.socket.client.Socket;
import javafx.util.Callback;
import okhttp3.OkHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import cz.zelenikr.remotetouch.security.AESCipher;
import cz.zelenikr.remotetouch.security.SymmetricCipher;
import cz.zelenikr.remotetouch.security.exception.UnsupportedCipherException;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.URI;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Roman Zelenik
 */
public class SocketIOClient implements Client {
    private static final Logger clientLogger = Logger.getLogger(SocketIOClient.class.getSimpleName());
    private static final String EVENT_EVENT = "event";

    static {
//        for (Handler handler : clientLogger.getHandlers()) clientLogger.removeHandler(handler);
//        ConsoleHandler handler = new ConsoleHandler();
//        handler.setLevel(Level.ALL);
//        clientLogger.addHandler(handler);

        clientLogger.setLevel(Level.ALL);

        if (Main.LOGGER_USE_FILE_HANDLER) {
            try {
                Utils.addFileHandler(clientLogger);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private final String clientToken;
    private final Socket socket;
    private final URI serverUri;
    private final SymmetricCipher symmetricCipher;

    private Callback<ConnectionStatus, Void> connectionStatusChangedListener;
    private ContentRecivedListener<CallEventContent> callContentReceivedListener;
    private ContentRecivedListener<NotificationEventContent> notificationContentReceivedListener;
    private ContentRecivedListener<SmsEventContent> smsContentReceivedListener;

    public SocketIOClient(String clientToken, URI uri, String secureKey) throws UnsupportedCipherException {
        IO.Options options = new IO.Options();
        options.secure = true;

//      Code for server self-signed certificate
        OkHttpClient okHttpClient = createSSL();
        options.callFactory = okHttpClient;
        options.webSocketFactory = okHttpClient;

        this.serverUri = uri;
        this.socket = IO.socket(uri, options);
        this.clientToken = clientToken;
        this.symmetricCipher = new AESCipher(secureKey);

        initSocket();
    }

    @Override
    public void connect() {
        socket.connect();
    }

    @Override
    public void disconnect() {
        socket.disconnect();
        notifyConnectionStatusChanged(ConnectionStatus.DISCONNECTED);
    }

    @Override
    public boolean isConnected() {
        return socket.connected();
    }

    @Override
    public void setOnConnectionStatusChanged(Callback<ConnectionStatus, Void> listener) {
        connectionStatusChangedListener = listener;
    }

    @Override
    public void setOnCallReceived(ContentRecivedListener<CallEventContent> listener) {
        callContentReceivedListener = listener;
    }

    @Override
    public void setOnNotificationReceived(ContentRecivedListener<NotificationEventContent> listener) {
        notificationContentReceivedListener = listener;
    }

    @Override
    public void setOnSMSReceived(ContentRecivedListener<SmsEventContent> listener) {
        smsContentReceivedListener = listener;
    }

    /**
     * Initializes {@link Socket} listeners.
     */
    private void initSocket() {
        socket.on(Socket.EVENT_CONNECTING, args -> {
            clientLogger.fine("connecting to " + serverUri);
            notifyConnectionStatusChanged(ConnectionStatus.CONNECTING);
        }).on(Socket.EVENT_CONNECT, args -> {
            clientLogger.fine("connected to " + serverUri);
            notifyConnectionStatusChanged(ConnectionStatus.CONNECTED);
            sendIntro();
        }).on(Socket.EVENT_CONNECT_ERROR, args -> {
            clientLogger.fine("connection error");
            notifyConnectionStatusChanged(ConnectionStatus.CONNECT_ERROR);
        }).on(Socket.EVENT_DISCONNECT, args -> {
            clientLogger.fine("disconnected from " + serverUri);
            notifyConnectionStatusChanged(ConnectionStatus.DISCONNECTED);
        }).on(Socket.EVENT_RECONNECTING, args -> {
            clientLogger.fine("reconnecting to " + serverUri);
            notifyConnectionStatusChanged(ConnectionStatus.RECONNECTING);
        }).on(Socket.EVENT_RECONNECT, args -> {
            clientLogger.fine("reconnected to " + serverUri);
            notifyConnectionStatusChanged(ConnectionStatus.RECONNECTED);
        }).on(Socket.EVENT_RECONNECT_ERROR, args -> {
            clientLogger.fine("reconnection error");
            notifyConnectionStatusChanged(ConnectionStatus.RECONNECT_ERROR);
        }).on(EVENT_EVENT, args -> {
            clientLogger.info("received 'event'");
            if (args.length > 0) {
                Object data = args[0];
                //logFine(data.toString());
                onEventReceived(data);
            }
        }).on(Socket.EVENT_MESSAGE, args -> clientLogger.info(args[0].toString())
        );
    }

    /**
     * This is needed if server is using self-signed certificate.
     *
     * @return
     */
    private OkHttpClient createSSL() {
        //Security.insertProviderAt(new BouncyCastleProvider(), 1);

        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());

            HostnameVerifier myHostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession sslSession) {
                    //clientLogger.info("HostnameVerifier for " + hostname);
                    return serverUri.getHost().equals(hostname);
                }
            };
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .hostnameVerifier(myHostnameVerifier)
                    .sslSocketFactory(sc.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                    .build();

            return okHttpClient;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    /**
     * Notifies registered listener of change of {@link ConnectionStatus}.
     *
     * @param newStatus the current status value
     */
    private void notifyConnectionStatusChanged(ConnectionStatus newStatus) {
        if (connectionStatusChangedListener != null) connectionStatusChangedListener.call(newStatus);
    }

    /**
     * Notifies registered {@link ContentRecivedListener ContentRecivedListener&lt;CallEventContent&gt;} of new call events.
     *
     * @param content the new call events
     */
    private void onCallReceived(CallEventContent... content) {
        clientLogger.info("call: " + Arrays.toString(content));
        if (callContentReceivedListener != null) callContentReceivedListener.onReceived(content);
    }

    /**
     * Processes received 'event' type {@link MessageDTO} from server. The message is expected in {@link JSONObject} format.
     *
     * @param data the received message like a JSON
     */
    private void onEventReceived(Object data) {
        if (data instanceof JSONObject) {
            // Read content from message
            JSONArray jsonArray = parseMessageContent((JSONObject) data);

            // Process array items
            for (Object jsonArrayItem : jsonArray) {

                // We expect only JSON objects
                if (!(jsonArrayItem instanceof JSONObject)) {
                    continue;
                }

                // Try parse EventDTO object from current array item
                EventDTO eventDTO = JsonMapper.eventFromJson((JSONObject) jsonArrayItem);
                if (eventDTO == null) {
                    continue;
                }

                // Select listener by EventType
                switch (eventDTO.getType()) {
                    case CALL:
                        onCallReceived((CallEventContent) eventDTO.getContent());
                        break;
                    case NOTIFICATION:
                        onNotificationReceived((NotificationEventContent) eventDTO.getContent());
                        break;
                    case SMS:
                        onSmsReceived((SmsEventContent) eventDTO.getContent());
                        break;
                    default:
                        clientLogger.severe("Unknown EventType: " + eventDTO.getType());
                }
            }
        }
    }

    /**
     * Notifies registered {@link ContentRecivedListener ContentRecivedListener&lt;NotificationEventContent&gt;} of new notifications.
     *
     * @param content the  new notifications
     */
    private void onNotificationReceived(NotificationEventContent... content) {
        clientLogger.info("notification: " + Arrays.toString(content));
        if (notificationContentReceivedListener != null) notificationContentReceivedListener.onReceived(content);
    }

    /**
     * Notifies registered {@link ContentRecivedListener ContentRecivedListener&lt;SmsEventContent&gt;} of new sms events.
     *
     * @param content the new sms events
     */
    private void onSmsReceived(SmsEventContent... content) {
        clientLogger.info("sms: " + Arrays.toString(content));
        if (smsContentReceivedListener != null) smsContentReceivedListener.onReceived(content);
    }

    /**
     * Reads and decrypts 'content' in the specific {@link JSONObject}. Returns decrypted 'content' like {@link JSONArray}.
     * If {@code json} doesn't contain 'content' or it's empty, this method returns empty {@link JSONArray}.
     *
     * @param json the given object to parsing
     * @return 'content' value of {@code json} like {@link JSONArray}
     */
    private JSONArray parseMessageContent(JSONObject json) {
        JSONArray contentArray = new JSONArray();

        // JSON is empty or doesn't contain 'content'
        if (json.length() == 0 || !json.has("content")) {
            return contentArray;
        }

        try {
            // Get 'content' (JSONArray) from JSON
            contentArray = json.optJSONArray("content");

            // Get encrypted text content
            String content = contentArray.optString(0);
            if (content.isEmpty()) {
                return new JSONArray();
            }

            // Decrypt text content
            content = decryptMessage(content);

            // Parse decrypted text to JSONArray of JSON objects
            contentArray = new JSONArray(content);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        return contentArray;
    }

    /**
     * Sends special ("pair") message to the server.
     */
    private void sendIntro() {
        JSONObject json = JsonMapper.toJSONObject(new MessageDTO(clientToken, ""));
        clientLogger.info("sendIntro " + json.toString());
        socket.emit("intro", json);
    }

    private String decryptMessage(String encrypted) {
        return symmetricCipher.decrypt(encrypted);
    }

}
