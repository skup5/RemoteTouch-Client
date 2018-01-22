
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * @author Roman Zelenik
 */
public class Main {

    static Logger logger = Logger.getLogger("Main");

    static int clientCount = 1;


    public static void main(String[] args) throws URISyntaxException {
        try {
            parseArgs(args);
        } catch (RuntimeException runtimeException) {
            printHelp();
            System.exit(1);
        }

        URI uri = new URI("http://localhost:3000");
        SocketIOClient[] clients = new SocketIOClient[clientCount];

        for (int i = 0; i < clientCount; i++) {
            clients[i] = new SocketIOClient(i + 1, uri);
            clients[i].run();
        }

        System.out.println("Stop clients by pressing any character key and enter...");
        new Scanner(System.in).hasNext();

        System.out.println("Stopping...");
        for (int i = 0; i < clientCount; i++) {
            clients[i].stop();
        }
    }

    private static void printHelp() {
        System.out.println("Usage: java -jar socket-io-client.jar [clients]");
    }

    private static void parseArgs(String[] args) {
        if (args.length > 0) {
            clientCount = Integer.parseInt(args[0]);
            if (clientCount < 1) throw new IllegalArgumentException("Number of clients have to be greater then 0");
        }
    }

    private static JSONObject toJson(Object object) {
        return new JSONObject(object);
    }

    static class SocketIOClient {
        final String tag;
        final int clientId;
        final URI uri;
        final Logger clientLogger = Logger.getLogger(getClass().getSimpleName());
        final Socket socket;

        public SocketIOClient(int clientId, URI uri) {
            super();
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
                                if (json.length() > 0) logInfo("sms:" + json.getString("content"));
                            }
                        }
                    }
            ).on(Socket.EVENT_DISCONNECT, args -> logInfo("disconnected"));
            socket.connect();
        }

        public void stop() {
            socket.disconnect();
        }

        private void sendIntro() {
            JSONObject json = toJson(new SimpleDTO(clientId, ""));
            logInfo("sendIntro " + json.toString());
            socket.emit("intro", json);
        }

        private void logInfo(String msg) {
            clientLogger.info(tag + msg);
        }
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
