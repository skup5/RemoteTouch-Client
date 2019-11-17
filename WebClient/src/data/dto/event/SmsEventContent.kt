package data.dto.event

/**
 * Represents SMS.
 *
 * @author Roman Zelenik
 */
data class SmsEventContent(
        /**
         * Name of sender/receiver.
         */
        val name: String,
        /**
         * Number of sender/receiver.
         */
        val number: String,
        /**
         * Content of SMS.
         */
        val content: String,
        /**
         * Timestamp of sending/receiving in milliseconds since the epoch.
         */
        val time: Long
) : EventContent
