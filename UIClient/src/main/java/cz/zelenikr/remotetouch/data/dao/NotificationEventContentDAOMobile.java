package cz.zelenikr.remotetouch.data.dao;

import cz.zelenikr.remotetouch.Callback;
import cz.zelenikr.remotetouch.data.event.NotificationEventContent;

/**
 * @author Roman Zelenik
 */
public class NotificationEventContentDAOMobile extends GenericMobileContentDAO<NotificationEventContent> implements NotificationEventContentDAO {

    private static NotificationEventContent[] mockData = {
            new NotificationEventContent("com.fake.app", "Mock application", "", "Mock app title", "lorem ipsum etcetera", System.currentTimeMillis() - 2000 * 60),
            new NotificationEventContent("com.fake.app2", "Fake messenger", "New message", "Fake messenger", "Santa wrote: Ho, ho, ho! Merry Christmas!", System.currentTimeMillis() - 3000 * 60),
            new NotificationEventContent("com.fake.app3", "", "", "com.fake.app3 title", "lorem ipsum etcetera", System.currentTimeMillis() - 5000)
    };

    private Callback<NotificationEventContent> newItemCallback;

    public NotificationEventContentDAOMobile() {
        super();
        connectionManager.registerNotificationReceivedListener(this::onNewNotification);
    }

    @Override
    public void loadAllAsync(Callback<NotificationEventContent[]> callback) {
        callback.call(mockData);
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
