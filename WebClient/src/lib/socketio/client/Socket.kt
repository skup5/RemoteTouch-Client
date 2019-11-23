package lib.socketio.client

import kotlin.js.Json


/**
 * The socket class for Socket.IO Client.
 */
external class Socket {

    /**
     * Connects the socket.
     */
    fun open(): Socket

    /**
     * Connects the socket.
     */
    fun connect(): Socket

    /**
     * Send messages.
     *
     * @param args data to send.
     * @return a reference to this object.
     */
    fun send(vararg args: Any): Socket

    /**
     * Emits an event. When you pass [Ack] at the last argument, then the acknowledge is done.
     *
     * @param event an event name.
     * @param args data to send.
     * @return a reference to this object.
     */
    fun emit(event: String, vararg args: Any?, ack: (param: Any) -> Unit? = definedExternally): Socket

    fun on(eventName: String, callback: (data: Json?) -> Unit): Socket

    /**
     * Disconnects the socket.
     *
     * @return a reference to this object.
     */
    fun close(): Socket

    /**
     * Disconnects the socket.
     *
     * @return a reference to this object.
     */
    fun disconnect(): Socket

    fun io(): Manager

    val connected: Boolean

    /**
     * A property on the socket instance that is equal to the underlying engine.io socket id.
     *
     * The value is present once the socket has connected, is removed when the socket disconnects and is updated if the socket reconnects.
     *
     * @return a socket id
     */
    val id: String?

}


