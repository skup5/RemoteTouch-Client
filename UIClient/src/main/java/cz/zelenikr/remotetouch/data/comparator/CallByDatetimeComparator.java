package cz.zelenikr.remotetouch.data.comparator;

import cz.zelenikr.remotetouch.data.event.CallEventContent;

import java.util.Comparator;

/**
 * Compares {@link CallEventContent} objects by the creation timestamp.
 * They will be sorted in descending order.
 *
 * @author Roman Zelenik
 */
public class CallByDatetimeComparator implements Comparator<CallEventContent> {
    @Override
    public int compare(CallEventContent o1, CallEventContent o2) {
        return Long.compare(o2.getWhen(), o1.getWhen());
    }
}
