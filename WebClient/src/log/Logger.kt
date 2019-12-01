package log

class Logger(val name: String) {

    companion object {
        fun getLogger(name: String): Logger {
            return Logger(name)
        }
    }

    fun debug(msg: String) {
        console.log(format(msg))
    }

    fun info(msg: String) {
        console.info(format(msg))
    }

    fun debug(msg: Number) {
        debug(msg.toString())
    }

    private fun format(msg: String) = "$name: $msg"
}