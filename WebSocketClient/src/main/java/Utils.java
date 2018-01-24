import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Roman Zelenik
 */
public class Utils {
    private Utils() {
    }

    private static final String EXTENSION = ".txt";
    private static final Level FILE_LEVEL = Level.ALL;
    private static final String FILE_NAME_PATTERN = "log_";
    private static final Date CURRENT_DATE = new Date();
    private static final Gson GSON = new Gson();

    public static String toJson(Object object) {
        return GSON.toJson(object);
    }

    public static <T> T fromJson(String json, Class<T> type) throws JsonSyntaxException {
        return GSON.fromJson(json, type);
    }

    public static void addFileHandler(Logger logger) throws IOException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss");

        FileHandler fileHandler = new FileHandler(FILE_NAME_PATTERN + dateFormat.format(CURRENT_DATE) + EXTENSION);
        fileHandler.setLevel(FILE_LEVEL);
        fileHandler.setFormatter(new FileFormatter());
        logger.addHandler(fileHandler);
    }

}
