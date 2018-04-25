package cz.zelenikr.remotetouch.network;

import cz.zelenikr.remotetouch.data.event.CallEventContent;
import cz.zelenikr.remotetouch.data.event.NotificationEventContent;
import cz.zelenikr.remotetouch.data.event.SmsEventContent;
import javafx.util.Callback;

/**
 * Represents communication with mobile device.
 *
 * @author Roman Zelenik
 */
public interface Client {

    /**
     * Connects client.
     */
    void connect();

    /**
     * Disconnects client.
     */
    void disconnect();

    boolean isConnected();

    void setOnConnectionStatusChanged(Callback<ConnectionStatus, Void> listener);

    void setOnCallReceived(ContentRecivedListener<CallEventContent> listener);

    void setOnNotificationReceived(ContentRecivedListener<NotificationEventContent> listener);

    void setOnSMSReceived(ContentRecivedListener<SmsEventContent> listener);

}
