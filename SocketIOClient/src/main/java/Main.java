
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Roman Zelenik
 */
public class Main {

    static Logger logger = Logger.getLogger("Main");
    static int clientCount = 4;
    public static final boolean LOGGER_USE_FILE_HANDLER = true;


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

}
