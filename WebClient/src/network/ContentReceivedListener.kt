package network

import data.dto.event.EventContent

/**
 * A class that implements this interface can be informed when [Client] received new content from mobile device.
 *
 * @param T type of received content object
 * @author Roman Zelenik
 */
interface ContentReceivedListener<T : EventContent> {
    /**
     * @param contents received content object(s)
     */
    fun onReceived(vararg contents: T)
}
