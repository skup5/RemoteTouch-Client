package cz.zelenikr.remotetouch;

import java.util.Locale;
import java.util.prefs.Preferences;

/**
 * @author Roman Zelenik
 */
public final class Settings {

    public static final String KEY_LOCALE = "locale";

    private static final String DEFAULT_LOCALE = "cs";

    private static Settings instance = new Settings();

    public static Settings getInstance() {
        return instance;
    }

    private final Preferences preferences;

    public Locale getLocale() {
        String lang = preferences.get(KEY_LOCALE, DEFAULT_LOCALE);
        return new Locale(lang);
    }

    public void setLocale(Locale locale) {
        preferences.put(KEY_LOCALE, locale.getLanguage());
    }

    private Settings() {
        this.preferences = Preferences.userRoot();
    }
}
