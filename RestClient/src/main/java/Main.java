

import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

/**
 * @author Roman Zelenik
 */
public class Main {

    /**
     * milliseconds
     **/
    private static final long DEFAULT_CLIENT_WAITING_TIME = 2000;

    private static final int DEFAULT_REQUEST_COUNT = 1;

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

        URL url = new URL("http://localhost:4000");
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

    private static String toJson(Object object) {
        return new Gson().toJson(object);
    }

    static class RestClientThread extends Thread {
        static final GsonFactory jsonFactory = new GsonFactory();

        final Logger threadLogger;
        final long clientId;
        final HttpTransport httpTransport = new NetHttpTransport();
        final GenericUrl url;

        int requestCount = DEFAULT_REQUEST_COUNT;
        long waitingTime = DEFAULT_CLIENT_WAITING_TIME;
        private volatile boolean stop = false;

        public RestClientThread(long clientId, URL url) {
            super();
            this.clientId = clientId;
            this.url = new GenericUrl(url);
            this.threadLogger = Logger.getLogger(getClass().getSimpleName() + "(" + clientId + ")");
        }

        public RestClientThread(String name, long clientId, URL url) {
            super(name);
            this.clientId = clientId;
            this.url = new GenericUrl(url);
            this.threadLogger = Logger.getLogger(getClass().getSimpleName() + "(" + clientId + ")");
        }

        @Override
        public void run() {
            String json = toJson(new SimpleDTO(clientId, "sms", "Přišla nová česká smska."));
            HttpContent httpContent = new ByteArrayContent("application/json", json.getBytes());

//            for (int requestCounter = requestCount; requestCounter > 0 && !stop; requestCounter--) {
            for (int requestCounter = requestCount; requestCounter > 0; requestCounter--) {
                makeRequest(httpContent);
                try {
                    sleep(waitingTime);
                } catch (InterruptedException e) {
                    stop = true;
                }
            }
            ;
        }

        public void requestStop() {
            this.stop = true;
        }

        public void setRequestCount(int requestCount) {
            this.requestCount = requestCount;
        }

        public void setWaitingTime(long waitingTime) {
            this.waitingTime = waitingTime;
        }

        private void makeRequest(HttpContent httpContent) {
            try {
                //threadLogger.info("post request");
                HttpResponse httpResponse = httpTransport.createRequestFactory()
                        .buildPostRequest(url, httpContent)
                        .setThrowExceptionOnExecuteError(false)
                        .execute();

                try {
                    if (!httpResponse.isSuccessStatusCode()) {
                        threadLogger.warning(httpResponse.getStatusCode() + " - " + httpResponse.getStatusMessage());
                    }
                } finally {
                    httpResponse.disconnect();
                }
            } catch (IOException e) {
//                e.printStackTrace();
                threadLogger.warning(e.getLocalizedMessage());
            }
        }
    }

    static class SimpleDTO implements Serializable {
        long id;
        String content;
        String event;

        public SimpleDTO() {
            this(0, "event");
        }

        public SimpleDTO(long id, String event) {
            this(id, event, "");
        }

        public SimpleDTO(long id, String event, String content) {
            this.content = content;
            this.event = event;
            this.id = id;
        }
    }
}
