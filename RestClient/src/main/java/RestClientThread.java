import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.logging.Logger;

/**
 * @author Roman Zelenik
 */
public class RestClientThread extends Thread {

    /**
     * milliseconds
     **/
    private static final long DEFAULT_CLIENT_WAITING_TIME = 2000;

    private final Logger threadLogger;
    private final long clientId;
    private final HttpTransport httpTransport = new NetHttpTransport();
    private final GenericUrl url;

    private int requestCount = 1;
    private long waitingTime = DEFAULT_CLIENT_WAITING_TIME;
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
        String json = toJson(new SimpleDTO(clientId, EEventType.NOTIFICATION, "Přišla nová česká smska."));
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

    private static String toJson(Object object) {
        return new Gson().toJson(object);
    }

    static class SimpleDTO implements Serializable {
        long id;
        String content;
        EEventType event;

        public SimpleDTO(long id, EEventType event) {
            this(id, event, "");
        }

        public SimpleDTO(long id, EEventType event, String content) {
            this.content = content;
            this.event = event;
            this.id = id;
        }
    }
}

