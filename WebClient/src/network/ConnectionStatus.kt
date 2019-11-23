package network

/**
 * Represents client connection state.
 *
 * @author Roman Zelenik
 */
enum class ConnectionStatus {
    /**
     * Client is connecting.
     */
    CONNECTING,
    /**
     * Client was successfully connected.
     */
    CONNECTED,
    /**
     * Error while connection.
     */
    CONNECT_ERROR,
    /**
     * Client is reconnecting.
     */
    RECONNECTING,
    /**
     * Client was successfully reconnected.
     */
    RECONNECTED,
    /**
     * Error while reconnection.
     */
    RECONNECT_ERROR,
    /**
     * Client was successfully disconnected.
     */
    DISCONNECTED
}
