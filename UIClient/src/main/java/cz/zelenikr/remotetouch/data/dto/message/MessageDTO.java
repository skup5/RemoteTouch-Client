package cz.zelenikr.remotetouch.data.dto.message;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Arrays;


/**
 * Represents REST server message.
 *
 * @author Roman Zelenik
 */
public class MessageDTO implements Serializable {

    private final String id;

    private final Serializable[] content;

    public MessageDTO(@NotNull String id) {
        this(id, "");
    }

    /**
     * @param id      Client identification token.
     * @param content Content of message.
     */
    public MessageDTO(@NotNull String id, @NotNull Serializable... content) {
        this.content = content;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Serializable[] getContent() {
        return content;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MessageDTO{");
        sb.append("id=").append(id);
        sb.append(", content='").append(Arrays.toString(content)).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
