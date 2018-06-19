package cz.zelenikr.remotetouch.data.dto.event;

import org.jetbrains.annotations.NotNull;

/**
 * Represents SMS.
 *
 * @author Roman Zelenik
 */
public class SmsEventContent implements EventContent {

    private final String name;

    private final String number;

    private final String content;

    private final long when;

    /**
     * @param name    Name of sender/receiver.
     * @param number  Number of sender/receiver.
     * @param content Content of SMS.
     * @param when    Timestamp of sending/receiving in milliseconds since the epoch.
     */
    public SmsEventContent(@NotNull String name, @NotNull String number, @NotNull String content, long when) {
        this.name = name;
        this.number = number;
        this.content = content;
        this.when = when;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public String getContent() {
        return content;
    }

    public long getWhen() {
        return when;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SmsEventContent{");
        sb.append("name='").append(name).append('\'');
        sb.append(", number='").append(number).append('\'');
        sb.append(", content='").append(content).append('\'');
        sb.append(", when=").append(when);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SmsEventContent content1 = (SmsEventContent) o;

        if (when != content1.when) return false;
        if (!name.equals(content1.name)) return false;
        if (!number.equals(content1.number)) return false;
        return content.equals(content1.content);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + number.hashCode();
        result = 31 * result + content.hashCode();
        result = 31 * result + (int) (when ^ (when >>> 32));
        return result;
    }
}
