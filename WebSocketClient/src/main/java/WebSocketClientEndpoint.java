
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Roman Zelenik
 */
@ClientEndpoint
public class WebSocketClientEndpoint {

    private static final Logger clientLogger = Logger.getLogger(WebSocketClientEndpoint.class.getSimpleName());

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
    private int rightSMSCounter = 0;
    private int wrongSMSCounter = 0;
    private final URI uri;
    private final WebSocketContainer container;
    private Session userSession = null;
    private MessageHandler messageHandler;

    public WebSocketClientEndpoint(int clientId, URI endpointURI) {
        this.clientId = clientId;
        this.uri = endpointURI;
        this.tag = "[" + clientId + "] ";
        this.container = ContainerProvider.getWebSocketContainer();
    }

    /**
     * Callback hook for Connection open events.
     *
     * @param userSession the userSession which is opened.
     */
    @OnOpen
    public void onOpen(Session userSession) {
        logInfo("connected");
        this.userSession = userSession;
        sendIntro();
    }

    /**
     * Callback hook for Connection close events.
     *
     * @param userSession the userSession which is getting closed.
     * @param reason      the reason for connection close
     */
    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        logInfo("disconnected");
        this.userSession = null;
    }

    /**
     * Callback hook for Message Events. This method will be invoked when a client send a message.
     *
     * @param json The text message
     */
    @OnMessage
    public void onMessage(String json) {
        MessageDTO msg = Utils.fromJson(json, MessageDTO.class);
        switch (msg.event) {
            case "sms":
                logFine(json);
                if (msg.id == clientId) rightSMSCounter++;
                else wrongSMSCounter++;
            default:
                break;
        }
    }

    public void run() throws IOException, DeploymentException {
        container.connectToServer(this, uri);
//        Logger.getLogger(getClass().getSimpleName()).info("connectToServer(" + uri.toASCIIString() + ")");
    }

    public void close() throws IOException {
        if (userSession.isOpen()) userSession.close();
        logInfo("received " + (rightSMSCounter + wrongSMSCounter) + " sms (" + wrongSMSCounter + " wrong)");
    }

    /**
     * register message handler
     *
     * @param msgHandler
     */
    public void addMessageHandler(MessageHandler msgHandler) {
        this.messageHandler = msgHandler;
    }

    /**
     * Send a message.
     *
     * @param message
     */
    public void sendMessage(String message) {
        this.userSession.getAsyncRemote().sendText(message);
    }

    private void sendIntro() {
        String json = Utils.toJson(new MessageDTO(clientId, "intro"));
        logInfo("sendIntro " + json);
        sendMessage(json);
    }

    private void logInfo(String msg) {
        clientLogger.info(tag + msg);
    }

    private void logFine(String msg) {
        clientLogger.fine(tag + msg);
    }

    public interface MessageHandler {

        void handleMessage(String message);
    }
}

