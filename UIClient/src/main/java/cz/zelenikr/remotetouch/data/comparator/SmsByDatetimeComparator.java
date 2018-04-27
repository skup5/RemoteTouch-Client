package cz.zelenikr.remotetouch.data.comparator;

import cz.zelenikr.remotetouch.data.dto.event.SmsEventContent;

import java.util.Comparator;

/**
 * Compares {@link SmsEventContent} objects by the creation timestamp.
 * They will be sorted in descending order.
 *
 * @author Roman Zelenik
 */
public class SmsByDatetimeComparator implements Comparator<SmsEventContent> {
    @Override
    public int compare(SmsEventContent o1, SmsEventContent o2) {
        return Long.compare(o2.getWhen(), o1.getWhen());
    }
}
