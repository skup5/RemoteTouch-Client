package cz.zelenikr.remotetouch.network;

import cz.zelenikr.remotetouch.data.dto.event.EventContent;

/**
 * A class that implements this interface can be informed when {@link Client} received new content from mobile device.
 *
 * @param <T> type of received content object
 * @author Roman Zelenik
 */
public interface ContentRecivedListener<T extends EventContent> {
    /**
     * @param contents received content object(s)
     */
    void onReceived(T... contents);
}
