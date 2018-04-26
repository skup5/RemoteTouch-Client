package cz.zelenikr.remotetouch.data.event;

import cz.zelenikr.remotetouch.data.CallType;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a call.
 *
 * @author Roman Zelenik
 */
public class CallEventContent implements EventContent {

    private final String name;

    private final String number;

    private final CallType type;

    private final long when;

    /**
     * @param name
     * @param number
     * @param type
     * @param when
     */
    public CallEventContent(@NotNull String name, @NotNull String number, @NotNull CallType type, long when) {
        this.name = name;
        this.number = number;
        this.type = type;
        this.when = when;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public CallType getType() {
        return type;
    }

    public long getWhen() {
        return when;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CallEventContent{");
        sb.append("name='").append(name).append('\'');
        sb.append(", number='").append(number).append('\'');
        sb.append(", type=").append(type);
        sb.append(", when=").append(when);
        sb.append('}');
        return sb.toString();
    }
}
