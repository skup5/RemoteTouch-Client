import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * @author Roman Zelenik
 */
public class FileFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date(record.getMillis()));
        return "[" +
                record.getLevel().getName() +
                "] " + time + " -> " +
                record.getMessage() + System.lineSeparator();
    }
}
