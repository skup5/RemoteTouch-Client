
import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * @author Roman Zelenik
 */
public class Main {

    static Logger logger = Logger.getLogger("Main");
    static int CLIENT_COUNT = 1, CLIENT_FIRST_ID = 1, PORT = 8080;
    static String HOSTNAME = "ws://localhost";
    static String SUBDOMAIN = "/jersey-server/socket";
    public static final boolean LOGGER_USE_FILE_HANDLER = true;

    public static void main(String[] args) throws URISyntaxException, IOException, DeploymentException {

        run();
    }

    private static void run() throws URISyntaxException, IOException, DeploymentException {
        URI uri = new URI(HOSTNAME + ":" + PORT + SUBDOMAIN);
        WebSocketClientEndpoint[] clients = new WebSocketClientEndpoint[CLIENT_COUNT];

        for (int i = 0; i < CLIENT_COUNT; i++) {
            // open websocket
            clients[i] = new WebSocketClientEndpoint(i + CLIENT_FIRST_ID, uri);
            clients[i].run();
        }


        System.out.println("Stop clients by pressing any character key and enter...");
        new Scanner(System.in).hasNext();

        System.out.println("Stopping...");

        for (int i = 0; i < CLIENT_COUNT; i++) {
            clients[i].close();
        }

    }
}
