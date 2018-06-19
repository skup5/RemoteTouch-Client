package cz.zelenikr.remotetouch.data.dto.event;


import org.jetbrains.annotations.NotNull;

/**
 * Represents Notification.
 *
 * @author Roman Zelenik
 */
public class NotificationEventContent implements EventContent {

    private final String app;

    private final String label;

    private final String title;

    private final String text;

    private final long when;

    /**
     * @param app    Application's package name of the current Notification.
     * @param label  Application's label of current Notification. Attribute of &lt;application&gt; tag in AndroidManifest file.
     * @param title  Title of the current Notification.
     * @param text   Text of the current Notification.
     * @param when   A timestamp related to the current Notification, in milliseconds since the epoch.
     */
    public NotificationEventContent(@NotNull String app, @NotNull String label, @NotNull String title, @NotNull String text, long when) {
        this.app = app;
        this.label = label;
        this.title = title;
        this.text = text;
        this.when = when;
    }

    public String getApp() {
        return app;
    }

    public String getLabel() {
        return label;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public long getWhen() {
        return when;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("NotificationEventContent{");
        sb.append("app='").append(app).append('\'');
        sb.append(", label='").append(label).append('\'');
        sb.append(", title='").append(title).append('\'');
        sb.append(", text='").append(text).append('\'');
        sb.append(", when=").append(when);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NotificationEventContent that = (NotificationEventContent) o;

        if (when != that.when) return false;
        if (!app.equals(that.app)) return false;
        if (!label.equals(that.label)) return false;
        if (!title.equals(that.title)) return false;
        return text.equals(that.text);
    }

    @Override
    public int hashCode() {
        int result = app.hashCode();
        result = 31 * result + label.hashCode();
        result = 31 * result + title.hashCode();
        result = 31 * result + text.hashCode();
        result = 31 * result + (int) (when ^ (when >>> 32));
        return result;
    }
}
