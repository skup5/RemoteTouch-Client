package cz.zelenikr.remotetouch.data.event;

import com.sun.istack.internal.NotNull;
import cz.zelenikr.remotetouch.data.CallType;

/**
 * Represents a call.
 *
 * @author Roman Zelenik
 */
public class CallEventContent implements EventContent {

    private final String name;

    private final String number;

    private final CallType type;

    /**
     * @param name
     * @param number
     * @param type
     */
    public CallEventContent(@NotNull String name, @NotNull String number, @NotNull CallType type) {
        this.name = name;
        this.number = number;
        this.type = type;
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CallEventContent{");
        sb.append("name='").append(name).append('\'');
        sb.append(", number='").append(number).append('\'');
        sb.append(", type=").append(type);
        sb.append('}');
        return sb.toString();
    }
}
