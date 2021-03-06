package cz.zelenikr.remotetouch.manager;

import cz.zelenikr.remotetouch.Callback;
import cz.zelenikr.remotetouch.data.dto.event.CallEventContent;
import cz.zelenikr.remotetouch.data.dto.event.NotificationEventContent;
import cz.zelenikr.remotetouch.data.dto.event.SmsEventContent;
import cz.zelenikr.remotetouch.network.Client;
import cz.zelenikr.remotetouch.network.ConnectionStatus;
import cz.zelenikr.remotetouch.network.ContentRecivedListener;
import cz.zelenikr.remotetouch.network.SocketIOClient;
import cz.zelenikr.remotetouch.security.Hash;
import cz.zelenikr.remotetouch.security.SHAHash;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

/**
 * Manages connection and communication with mobile device via {@link Client}.
 *
 * @author Roman Zelenik
 */
public final class ConnectionManager {
    private static final SettingsManager SETTINGS = SettingsManager.getInstance();
    private static final ConnectionManager INSTANCE = new ConnectionManager();

    public static ConnectionManager getInstance() {
        return INSTANCE;
    }

    private Client client;

    // ContentRecivedListener sets
    private Set<ContentRecivedListener<CallEventContent>> callReceivedListeners = new HashSet<>();
    private Set<ContentRecivedListener<NotificationEventContent>> notificationReceivedListeners = new HashSet<>();
    private Set<ContentRecivedListener<SmsEventContent>> smsReceivedListeners = new HashSet<>();

    // Connection state changed listeners
    private Set<Callback<ConnectionStatus>> connectionStateChangedListeners = new HashSet<>();

    /**
     * Connects to a device. Client is initialized just once when this method is called.
     * If client is already connected, nothing happen.
     */
    public void connect() {
        if (client == null) {
            client = initClient();
        }
        if (!client.isConnected()) {
            client.connect();
        }
    }

    /**
     * Disconnects a device.
     */
    public void disconnect() {
        if (client != null) {
            client.disconnect();
            client = null;
        }
    }

    /**
     * Disconnects and establishes new connection to a mobile device.
     */
    public void reconnect() {
        if (client != null) {
            disconnect();
        }
        connect();
    }

    public void registerConnectionStateChangedListener(@NotNull Callback<ConnectionStatus> listener) {
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

    public void removeConnectionStateChangedListener(@NotNull Callback<ConnectionStatus> listener) {
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

    private Client initClient() {
        SocketIOClient client = new SocketIOClient(loadClientToken(), loadServerAddress(), loadPairKey());
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
        return SETTINGS.getPairKey();
    }

    private URI loadServerAddress() {
        try {
            return SETTINGS.getServerAddress().toURI();
        } catch (URISyntaxException e) {
            //
            return null;
        }
    }

    private String loadClientToken() {
        return createToken(SETTINGS.getDeviceName(), loadPairKey());
    }

    private static String createToken(String device, String pairKey) {
        Hash hash = new SHAHash();
        return hash.hash(device + pairKey);
    }

    private ConnectionManager() {
    }
}
