package data.dto.message


/**
 * Represents REST server message.
 *
 * @author Roman Zelenik
 */
data class MessageDTO(val id: String, var content: Array<MessageContent> = emptyArray()) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class.js != other::class.js) return false

        other as MessageDTO

        if (id != other.id) return false
        if (!content.contentEquals(other.content)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + content.contentHashCode()
        return result
    }
}


