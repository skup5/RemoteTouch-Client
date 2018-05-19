package cz.zelenikr.remotetouch.data.dao;

import cz.zelenikr.remotetouch.Callback;
import cz.zelenikr.remotetouch.data.dto.event.NotificationEventContent;

/**
 * Provides access to a mobile notifications stored in remote mobile phone.
 *
 * @author Roman Zelenik
 */
public class NotificationEventContentDAOMobile extends GenericMobileContentDAO<NotificationEventContent> implements NotificationEventContentDAO {

    private Callback<NotificationEventContent> newItemCallback;

    public NotificationEventContentDAOMobile() {
        super();
        connectionManager.registerNotificationReceivedListener(this::onNewNotification);
    }

    @Override
    public void setOnNewItemCallback(Callback<NotificationEventContent> callback) {
        newItemCallback = callback;
    }

    private void onNewNotification(NotificationEventContent... notificationEventContents) {
        if (newItemCallback != null) {
            for (NotificationEventContent content : notificationEventContents) {
                newItemCallback.call(content);
            }
        }
    }

}
