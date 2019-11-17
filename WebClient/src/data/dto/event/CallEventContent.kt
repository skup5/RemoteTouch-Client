package data.dto.event

import data.dto.CallType


/**
 * Represents a phone call.
 *
 * @author Roman Zelenik
 */
data class CallEventContent(
        /**
         * Name of sender/receiver.
         */
        val name: String,
        /**
         * Number of sender/receiver.
         */
        val number: String,
        /**
         * Type of phone call.
         */
        val type: CallType,
        /**
         * Timestamp of incoming/outgoing call in milliseconds since the epoch.
         */
        val time: Long
) : EventContent
