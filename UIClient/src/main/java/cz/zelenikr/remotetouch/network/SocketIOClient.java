package cz.zelenikr.remotetouch.network;

import cz.zelenikr.remotetouch.Main;
import cz.zelenikr.remotetouch.Utils;
import cz.zelenikr.remotetouch.data.EventType;
import cz.zelenikr.remotetouch.data.SimpleMessageDTO;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.OkHttpClient;
import org.json.JSONObject;
import cz.zelenikr.remotetouch.security.AESCipher;
import cz.zelenikr.remotetouch.security.SymmetricCipher;
import cz.zelenikr.remotetouch.security.exception.UnsupportedCipherException;
import javax.net.ssl.*;
import java.io.IOException;
import java.net.URI;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Roman Zelenik
 */
public class SocketIOClient {
    private static final Logger clientLogger = Logger.getLogger(SocketIOClient.class.getSimpleName());

    static {
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
    private int rightSMSCounter = 0;
    private int wrongSMSCounter = 0;

    public SocketIOClient(String clientToken, URI uri, String secureKey) throws UnsupportedCipherException {
        IO.Options options = new IO.Options();
        options.secure = true;

//      Code for server self-signed certificate
//        OkHttpClient okHttpClient = createSSL();
//        options.callFactory = okHttpClient;
//        options.webSocketFactory = okHttpClient;

        this.serverUri = uri;
        this.socket = IO.socket(uri, options);
        this.clientToken = clientToken;
        this.tag = "";
        this.symmetricCipher = new AESCipher(secureKey);
    }

    public void run() {
        socket.on(Socket.EVENT_CONNECTING, args -> logInfo("connecting to " + serverUri)
        ).on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                logInfo("connected to " + serverUri);
                sendIntro();
            }
        }).on(Socket.EVENT_DISCONNECT, args -> logInfo("disconnected from " + serverUri)
        ).on("event", args -> {
            //logInfo("received 'event'");
            if (args.length > 0) {
                Object data = args[0];
                //logFine(data.toString());
                if (data instanceof String) {

                } else if (data instanceof JSONObject) {
                    JSONObject json = (JSONObject) data;
                    if (json.length() > 0) logFine("json response:" + json.getString("res"));
                }
            }
        }).on(EventType.SMS.name(), (args) -> {
            // logInfo("received 'sms'");
            if (args.length > 0) {
                Object data = args[0];
                //   logInfo(data.toString());
                if (data instanceof String) {

                } else if (data instanceof JSONObject) {
                    JSONObject json = (JSONObject) data;
                    if (json.length() > 0) {
                        String content = json.optString("content");
                        if (!content.isEmpty()) content = decryptMessage(content);
                        logInfo("sms:" + content);
//                        logFine(json.toString());
                        if (clientToken.equals(json.getString("id"))) rightSMSCounter++;
                        else wrongSMSCounter++;
                    }
                }
            }
        }).on(EventType.NOTIFICATION.name(), (args) -> {
            //logInfo("received 'notification'");
            if (args.length > 0) {
                Object data = args[0];
                //logFine(data.toString());
                if (data instanceof String) {

                } else if (data instanceof JSONObject) {
                    JSONObject json = (JSONObject) data;
                    if (json.length() > 0) {
                        String content = json.optString("content");
                        if (!content.isEmpty()) content = decryptMessage(content);
                        logInfo("notification:" + content);
                    }
                }
            }
        }).on(EventType.CALL.name(), args -> {
            //logInfo("received 'call'");
            if (args.length > 0) {
                Object data = args[0];
                //logFine(data.toString());
                if (data instanceof String) {

                } else if (data instanceof JSONObject) {
                    JSONObject json = (JSONObject) data;
                    if (json.length() > 0) {
                        String content = json.optString("content");
                        if (!content.isEmpty()) content = decryptMessage(content);
                        logInfo("call:" + content);
                    }
                }
            }
        }).on(Socket.EVENT_MESSAGE, args -> logInfo(args[0].toString())
        );
        socket.connect();
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

    public void close() {
        socket.disconnect();
        logInfo("received " + (rightSMSCounter + wrongSMSCounter) + " sms (" + wrongSMSCounter + " wrong)");
    }

    private void sendIntro() {
        JSONObject json = Utils.toJson(new SimpleMessageDTO(clientToken, ""));
        logInfo("sendIntro " + json.toString());
        socket.emit("intro", json);
    }

    private String decryptMessage(String encrypted) {
        return symmetricCipher.decrypt(encrypted);
    }

    private void logInfo(String msg) {
        clientLogger.info(tag + msg);
    }

    private void logFine(String msg) {
        clientLogger.fine(tag + msg);
    }


}
