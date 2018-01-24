
import java.io.Serializable;

/**
 * @author Roman Zelenik
 */
public class MessageDTO implements Serializable{
    public int id;
    public String content;
    public String event;

    public MessageDTO() {
        this(0, "event");
    }

    public MessageDTO(int id, String event) {
        this(id, event, "");
    }

    public MessageDTO(int id, String event, String content) {
        this.content = content;
        this.event = event;
        this.id = id;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MessageDTO{");
        sb.append("id=").append(id);
        sb.append(", content='").append(content).append('\'');
        sb.append(", event='").append(event).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
