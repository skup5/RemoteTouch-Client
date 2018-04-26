package cz.zelenikr.remotetouch.data.comparator;

import cz.zelenikr.remotetouch.data.event.NotificationEventContent;

import java.util.Comparator;

/**
 * Compares {@link NotificationEventContent} objects by the creation timestamp.
 * They will be sorted in descending order.
 *
 * @author Roman Zelenik
 */
public class NotificationByDatetimeComparator implements Comparator<NotificationEventContent> {
    @Override
    public int compare(NotificationEventContent o1, NotificationEventContent o2) {
        return Long.compare(o2.getWhen(), o1.getWhen());
    }
}
