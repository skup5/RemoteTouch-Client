package lib.socketio.client


/**
 * Called on a successful connection.
 */
const val EVENT_OPEN = "open"

/**
 * Called on a disconnection.
 */
const val EVENT_CLOSE = "close"

const val EVENT_PACKET = "packet"

/**
 * Called on a connection error.
 */
const val EVENT_ERROR = "error"

/**
 * Called on a connection error.
 */
const val EVENT_CONNECT_ERROR = "connect_error"

/**
 * Called on a connection timeout.
 */
const val EVENT_CONNECT_TIMEOUT = "connect_timeout"

/**
 * Called on a successful reconnection.
 */
const val EVENT_RECONNECT = "reconnect"

/**
 * Called on a reconnection attempt error.
 */
const val EVENT_RECONNECT_ERROR = "reconnect_error"

const val EVENT_RECONNECT_FAILED = "reconnect_failed"

const val EVENT_RECONNECT_ATTEMPT = "reconnect_attempt"

const val EVENT_RECONNECTING = "reconnecting"

const val EVENT_PING = "ping"

const val EVENT_PONG = "pong"

/**
 * Called on a connection.
 */
const val EVENT_CONNECT = "connect"

const val EVENT_CONNECTING = "connecting"

/**
 * Called on a disconnection.
 */
const val EVENT_DISCONNECT = "disconnect"

const val EVENT_MESSAGE = "message"
