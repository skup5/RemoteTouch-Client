package cz.zelenikr.remotetouch.data.formatter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Defines how the event timestamp will be presented to the user.
 *
 * @author Roman Zelenik
 */
public class EventDateTimeFormat extends SimpleDateFormat {

    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

    public EventDateTimeFormat() {
        super();
    }

    public String format(long when) {
        Date date = new Date(when);
        return isToday(date) ? TIME_FORMAT.format(date) : format(date);
    }

    private boolean isToday(Date date) {
        return getToday().before(date);
    }

    private Date getToday() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        return today.getTime();
    }
}
