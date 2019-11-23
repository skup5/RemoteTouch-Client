package lib.socketio.client

object IO {

    val io = kotlinext.js.require("socket.io-client")

    /**
     * Initializes a [Socket] from an existing [Manager] for multiplexing.
     *
     * @param uri uri to connect.
     * @param opts options for socket.
     * @return [Socket] instance.
     */
    fun socket(uri: String, opts: Options? = null): Socket {
        return io(uri, opts)
    }

    interface Options : Manager.Options {

        var forceNew: Boolean

        /**
         * Whether to enable multiplexing. Default is true.
         */
        var multiplex: Boolean
    }
}