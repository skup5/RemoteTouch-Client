package lib.socketio.client


/**
 * Manager class represents a connection to a given Socket.IO server.
 */
external interface Manager {

    fun reconnection(): Boolean

    fun reconnection(v: Boolean): Manager

    fun reconnectionAttempts(): Int

    fun reconnectionAttempts(v: Int): Manager

    fun reconnectionDelay(): Long

    fun reconnectionDelay(v: Long): Manager

    fun randomizationFactor(): Double

    fun randomizationFactor(v: Double): Manager

    fun reconnectionDelayMax(): Long

    fun reconnectionDelayMax(v: Long): Manager

    fun timeout(): Long

    fun timeout(v: Long): Manager

    /**
     * Connects the client.
     *
     * @param fn callback.
     * @return a reference to this object.
     */
    fun open(fn: ()->Unit?): Manager

    /**
     * Initializes [Socket] instances for each namespaces.
     *
     * @param nsp namespace.
     * @param opts options.
     * @return a socket instance for the namespace.
     */
    fun socket(nsp: String, opts: Options?): Socket

    open interface Options {

        var reconnection:Boolean
        var reconnectionAttempts: Int
        var reconnectionDelay: Long
        var reconnectionDelayMax: Long
        var randomizationFactor: Double
        var encoder: Any?
        var decoder: Any?

        /**
         * Connection timeout (ms). Set -1 to disable.
         */
        var timeout: Long
    }

}