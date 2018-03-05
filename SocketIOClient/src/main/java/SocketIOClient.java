import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONObject;
import security.AESCipher;
import security.SymmetricCipher;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
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

    private final String tag;
    private final int clientId;
    private final Socket socket;
    private final URI serverUri;
    private final SymmetricCipher symmetricCipher;
    private int rightSMSCounter = 0;
    private int wrongSMSCounter = 0;

    public SocketIOClient(int clientId, URI uri, String secureKey) throws NoSuchAlgorithmException, AESCipher.UnsupportedCipherException {
        this.serverUri = uri;
        this.socket = IO.socket(uri);
        this.clientId = clientId;
        this.tag = "[" + clientId + "] ";
        this.symmetricCipher = new AESCipher(secureKey);
    }

    public void run() {
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
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
                        if (json.getInt("id") == clientId) rightSMSCounter++;
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
                    if (json.length() > 0){
                        String content = json.optString("content");
                        if (!content.isEmpty()) content = decryptMessage(content);
                        logInfo("call:" + content);
                    }
                }
            }
        });
        socket.connect();
    }

    public void close() {
        socket.disconnect();
        logInfo("received " + (rightSMSCounter + wrongSMSCounter) + " sms (" + wrongSMSCounter + " wrong)");
    }

    private void sendIntro() {
        JSONObject json = Utils.toJson(new SimpleDTO(clientId, ""));
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

    public static class SimpleDTO implements Serializable {
        int id = 0;
        String msg = "";

        public SimpleDTO() {
        }

        public SimpleDTO(int id, String msg) {
            this.msg = msg;
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public String getMsg() {
            return msg;
        }

    }

}
