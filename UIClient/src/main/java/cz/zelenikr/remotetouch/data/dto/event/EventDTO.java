package cz.zelenikr.remotetouch.data.dto.event;


import org.jetbrains.annotations.NotNull;
import cz.zelenikr.remotetouch.data.dto.message.MessageContent;

/**
 * Represents some new event (like sms or call) on mobile phone.
 *
 * @author Roman Zelenik
 */
public class EventDTO implements MessageContent {

    private EventType type;

    private EventContent content;

    public EventDTO(@NotNull EventType type, @NotNull EventContent content) {
        this.type = type;
        this.content = content;
    }

    public EventType getType() {
        return type;
    }

    public EventContent getContent() {
        return content;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("EventDTO{");
        sb.append("type=").append(type);
        sb.append(", content=").append(content);
        sb.append('}');
        return sb.toString();
    }
}
