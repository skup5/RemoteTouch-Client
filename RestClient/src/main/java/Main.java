

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

/**
 * @author Roman Zelenik
 */
public class Main {


    private static final int DEFAULT_REQUEST_COUNT = 1;

//    private static final String URL = "http://localhost:8080/notification";
    private static final String URL = "http://remote-touch.azurewebsites.net/notification";

    static Logger logger = Logger.getLogger("Main");
    static int
            clientCount = 1,
            requestCount = DEFAULT_REQUEST_COUNT;

    public static void main(String[] args) throws MalformedURLException, InterruptedException {
        try {
            parseArgs(args);
        } catch (RuntimeException runtimeException) {
            System.out.println(runtimeException.getLocalizedMessage());
            printHelp();
            System.exit(1);
        }

        URL url = new URL(URL);
        RestClientThread[] clients = new RestClientThread[clientCount];

        for (int i = 0; i < clientCount; i++) {
            clients[i] = new RestClientThread(i + 1, url);
            clients[i].setRequestCount(requestCount);
            clients[i].start();
        }

//        System.out.println("Stop clients by pressing any character key and enter...");
//        new Scanner(System.in).hasNext();

//        System.out.println("Stopping...");
        for (int i = 0; i < clientCount; i++) {
//            clients[i].requestStop();
            clients[i].join();
        }
    }

    private static void printHelp() {
        System.out.println("Usage: java -jar rest-client.jar [clients] [requests]");
    }

    private static void parseArgs(String[] args) {
        if (args.length > 0) {
            clientCount = Integer.parseInt(args[0]);
            if (clientCount < 1) throw new IllegalArgumentException("Number of clients have to be greater then 0");

            requestCount = args.length >= 2 ? Integer.parseInt(args[1]) : requestCount;
            if (requestCount < 1) throw new IllegalArgumentException("Number of requests have to be greater then 0");

        }
    }

}
