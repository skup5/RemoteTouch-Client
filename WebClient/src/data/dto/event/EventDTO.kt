package data.dto.event

import data.dto.message.MessageContent


/**
 * Represents some new event (like sms or call) on mobile phone.
 *
 * @author Roman Zelenik
 */
data class EventDTO(val type: EventType, val content: EventContent) : MessageContent
