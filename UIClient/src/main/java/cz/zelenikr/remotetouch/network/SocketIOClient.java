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
import org.json.JSONObject;
import cz.zelenikr.remotetouch.security.AESCipher;
import cz.zelenikr.remotetouch.security.SymmetricCipher;
import cz.zelenikr.remotetouch.security.exception.UnsupportedCipherException;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.URI;
import java.security.cert.X509Certificate;
import java.util.Arrays;
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

    /**
     * Log message prefix
     */
    private final String tag;
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
        this.tag = "";
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
            logDebug("connecting to " + serverUri);
            notifyConnectionStatusChanged(ConnectionStatus.CONNECTING);
        }).on(Socket.EVENT_CONNECT, args -> {
            logDebug("connected to " + serverUri);
            notifyConnectionStatusChanged(ConnectionStatus.CONNECTED);
            sendIntro();
        }).on(Socket.EVENT_CONNECT_ERROR, args -> {
            logDebug("connection error");
            notifyConnectionStatusChanged(ConnectionStatus.CONNECT_ERROR);
        }).on(Socket.EVENT_DISCONNECT, args -> {
            logDebug("disconnected from " + serverUri);
            notifyConnectionStatusChanged(ConnectionStatus.DISCONNECTED);
        }).on(Socket.EVENT_RECONNECTING, args -> {
            logDebug("reconnecting to " + serverUri);
            notifyConnectionStatusChanged(ConnectionStatus.RECONNECTING);
        }).on(Socket.EVENT_RECONNECT, args -> {
            logDebug("reconnected to " + serverUri);
            notifyConnectionStatusChanged(ConnectionStatus.RECONNECTED);
        }).on(Socket.EVENT_RECONNECT_ERROR, args -> {
            logDebug("reconnection error");
            notifyConnectionStatusChanged(ConnectionStatus.RECONNECT_ERROR);
        }).on(EVENT_EVENT, args -> {
            logInfo("received 'event'");
            if (args.length > 0) {
                Object data = args[0];
                //logFine(data.toString());
                onEventReceived(data);
            }
        }).on(Socket.EVENT_MESSAGE, args -> logInfo(args[0].toString())
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
                    //logInfo("HostnameVerifier for " + hostname);
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

    private void notifyConnectionStatusChanged(ConnectionStatus newStatus) {
        if (connectionStatusChangedListener != null) connectionStatusChangedListener.call(newStatus);
    }

    private void onCallReceived(CallEventContent... content) {
        logInfo("call: " + Arrays.toString(content));
        if (callContentReceivedListener != null) callContentReceivedListener.onReceived(content);
    }

    private void onEventReceived(Object data) {
        if (data instanceof String) {

        } else if (data instanceof JSONObject) {
            JSONObject json = (JSONObject) data;
            if (json.length() > 0) {
                String content = json.optString("content");
                if (content.isEmpty()) {
                    logInfo("Empty content");
                } else {
                    content = decryptMessage(content);
                    EventDTO eventDTO = JsonMapper.eventFromJsonString(content);
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
                            logError("Unknown EventType: " + eventDTO.getType());
                    }
                }
            }
        }
    }

    private void onNotificationReceived(NotificationEventContent... content) {
        logInfo("notification: " + Arrays.toString(content));
        if (notificationContentReceivedListener != null) notificationContentReceivedListener.onReceived(content);
    }

    private void onSmsReceived(SmsEventContent... content) {
        logInfo("sms: " + Arrays.toString(content));
        if (smsContentReceivedListener != null) smsContentReceivedListener.onReceived(content);
    }

    private void sendIntro() {
        JSONObject json = JsonMapper.toJSONObject(new MessageDTO(clientToken, ""));
        logInfo("sendIntro " + json.toString());
        socket.emit("intro", json);
    }

    private String decryptMessage(String encrypted) {
        return symmetricCipher.decrypt(encrypted);
    }

    private void logInfo(String msg) {
        clientLogger.info(tag + msg);
    }

    private void logDebug(String msg) {
        clientLogger.fine(tag + msg);
    }

    private void logError(String error) {
        clientLogger.severe(tag + error);
    }

}
