package data.dto.event

/**
 * @author Roman Zelenik
 */
enum class EventType {
    CALL, SMS, NOTIFICATION;

    override fun toString(): String {
        return super.toString().toLowerCase()
    }
}
