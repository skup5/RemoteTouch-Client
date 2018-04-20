package cz.zelenikr.remotetouch.manager;

import com.sun.istack.internal.NotNull;
import cz.zelenikr.remotetouch.Main;
import cz.zelenikr.remotetouch.data.event.CallEventContent;
import cz.zelenikr.remotetouch.data.event.NotificationEventContent;
import cz.zelenikr.remotetouch.data.event.SmsEventContent;
import cz.zelenikr.remotetouch.network.Client;
import cz.zelenikr.remotetouch.network.ConnectionStatus;
import cz.zelenikr.remotetouch.network.ContentRecivedListener;
import cz.zelenikr.remotetouch.network.SocketIOClient;
import javafx.util.Callback;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Roman Zelenik
 */
public class ConnectionManager {
    private static ConnectionManager ourInstance = new ConnectionManager();

    public static ConnectionManager getInstance() {
        return ourInstance;
    }

    private Client client;

    // ContentRecivedListener sets
    private Set<ContentRecivedListener>
            callReceivedListeners = new HashSet<>(),
            notificationReceivedListeners = new HashSet<>(),
            smsReceivedListeners = new HashSet<>();
    // Connection state changed listeners
    private Set<Callback<ConnectionStatus, Void>> connectionStateChangedListeners = new HashSet<>();

    public void connect() {
        if (client == null) {
            try {
                client = initClient();
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
        if (!client.isConnected()) {
            client.connect();
        }

    }

    public void disconnect() {
        if (client != null) {
            client.disconnect();
        }
    }

    public void reconnect() {
        if (client != null && client.isConnected()) {
            client.disconnect();
        }
        connect();
    }

    public void registerConnectionStateChangedListener(@NotNull Callback<ConnectionStatus, Void> listener) {
        connectionStateChangedListeners.add(listener);
    }

    public void registerCallReceivedListener(@NotNull ContentRecivedListener<CallEventContent> listener) {
        callReceivedListeners.add(listener);
    }

    public void registerNotificationReceivedListener(@NotNull ContentRecivedListener<NotificationEventContent> listener) {
        notificationReceivedListeners.add(listener);
    }

    public void registerSmsReceivedListener(@NotNull ContentRecivedListener<SmsEventContent> listener) {
        smsReceivedListeners.add(listener);
    }

    public void removeConnectionStateChangedListener(@NotNull Callback<ConnectionStatus, Void> listener) {
        connectionStateChangedListeners.remove(listener);
    }

    public void removeCallReceivedListener(@NotNull ContentRecivedListener<CallEventContent> listener) {
        callReceivedListeners.remove(listener);
    }

    public void removeNotificationReceivedListener(@NotNull ContentRecivedListener<NotificationEventContent> listener) {
        notificationReceivedListeners.remove(listener);
    }

    public void removeSmsReceivedListener(@NotNull ContentRecivedListener<SmsEventContent> listener) {
        smsReceivedListeners.remove(listener);
    }

    private Client initClient() throws URISyntaxException {
        SocketIOClient client = new SocketIOClient(loadClientToken(), loadServerURL(), loadPairKey());
        client.setOnCallReceived(contents -> notifyCallReceived(contents));
        client.setOnNotificationReceived(contents -> notifyNotificationReceived(contents));
        client.setOnSMSReceived(contents -> notifySmsReceived(contents));
        client.setOnConnectionStatusChanged(status -> notifyConnectionStateChanged(status));
        return client;
    }

    private Void notifyConnectionStateChanged(ConnectionStatus status) {
        connectionStateChangedListeners.forEach(callback -> callback.call(status));
        return null;
    }

    private void notifyCallReceived(CallEventContent[] contents) {
        callReceivedListeners.forEach(contentRecivedListener -> contentRecivedListener.onReceived(contents));
    }

    private void notifyNotificationReceived(NotificationEventContent[] contents) {
        notificationReceivedListeners.forEach(contentRecivedListener -> contentRecivedListener.onReceived(contents));
    }

    private void notifySmsReceived(SmsEventContent[] contents) {
        smsReceivedListeners.forEach(contentRecivedListener -> contentRecivedListener.onReceived(contents));
    }

    private String loadPairKey() {
        return Main.SECURE_KEY;
    }

    private URI loadServerURL() throws URISyntaxException {
        return Main.getServerURI();
    }

    private String loadClientToken() {
        return Main.createToken(Main.DEVICE, Main.SECURE_KEY);
    }

    private ConnectionManager() {
    }
}
