import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.util.logging.Logger;

/**
 * @author Roman Zelenik
 */
public class SocketIOClient {
    private static final Logger clientLogger = Logger.getLogger(SocketIOClient.class.getSimpleName());

    static {
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
    private final URI uri;
    private final Socket socket;
    private int rightSMSCounter = 0;
    private int wrongSMSCounter = 0;

    public SocketIOClient(int clientId, URI uri) {
        this.socket = IO.socket(uri);
        this.clientId = clientId;
        this.uri = uri;
        this.tag = "[" + clientId + "] ";
    }

    public void run() {
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                logInfo("connected");
                sendIntro();
            }

        }).on("event", new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                logInfo("received 'event'");
                if (args.length > 0) {
                    Object data = args[0];
                    logInfo(data.toString());
                    if (data instanceof String) {

                    } else if (data instanceof JSONObject) {
                        JSONObject json = (JSONObject) data;
                        if (json.length() > 0) logInfo("json response:" + json.getString("res"));
                    }
                }
            }

        }).on("sms", (args) -> {
                    // logInfo("received 'sms'");
                    if (args.length > 0) {
                        Object data = args[0];
                        //   logInfo(data.toString());
                        if (data instanceof String) {

                        } else if (data instanceof JSONObject) {
                            JSONObject json = (JSONObject) data;
                            if (json.length() > 0) {
                                logInfo("sms:" + json.getString("content"));
                                if (json.getInt("id") == clientId) rightSMSCounter++;
                                else wrongSMSCounter++;
                            }
                        }
                    }
                }
        ).on(Socket.EVENT_DISCONNECT, args -> logInfo("disconnected"));
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

    private void logInfo(String msg) {
        clientLogger.info(tag + msg);
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
