package cz.zelenikr.remotetouch;

import com.sun.istack.internal.NotNull;

import java.util.Locale;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * This is a singleton that allows components to access the application settings.
 *
 * @author Roman Zelenik
 */
public final class Settings {

    public static final String
            KEY_LOCALE_LANG = "locale_lang",
            KEY_LOCAL_COUNTRY = "locale_country",
            KEY_DEVICE_NAME = "device_name",
            KEY_PAIR_KEY = "pair_key";

    private static final String
            DEF_LOCALE_LANG = "cs",
            DEF_LOCALE_COUNTRY = "CZ",
            DEF_DEVICE_NAME = "",
            DEF_PAIR_KEY = "";

    private static final Logger LOGGER = Logger.getLogger(Settings.class.getSimpleName());

    private static Settings instance = new Settings();

    public static Settings getInstance() {
        return instance;
    }

    private final Preferences preferences;

    public boolean containsDeviceName() {
        return contains(KEY_DEVICE_NAME);
    }

    public String getDeviceName() {
        return preferences.get(KEY_DEVICE_NAME, DEF_DEVICE_NAME);
    }

    public void setDeviceName(@NotNull String deviceName) {
        LOGGER.info("store " + deviceName);
        preferences.put(KEY_DEVICE_NAME, deviceName);
    }

    public boolean containsPairKey() {
        return contains(KEY_PAIR_KEY);
    }

    public String getPairKey() {
        return preferences.get(KEY_PAIR_KEY, DEF_PAIR_KEY);
    }

    public void setPairKey(@NotNull String pairKey) {
        LOGGER.info("store " + pairKey);
        preferences.put(KEY_PAIR_KEY, pairKey);
    }

    private boolean contains(String key) {
        try {
            for (String prefKey : preferences.keys()) {
                if (prefKey.equals(key)) return true;
            }
        } catch (BackingStoreException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Locale getLocale() {
        String lang = preferences.get(KEY_LOCALE_LANG, DEF_LOCALE_LANG);
        String country = preferences.get(KEY_LOCAL_COUNTRY, DEF_LOCALE_COUNTRY);
        return new Locale(lang, country);
    }

    public void setLocale(@NotNull Locale locale) {
        preferences.put(KEY_LOCALE_LANG, locale.getLanguage());
        preferences.put(KEY_LOCAL_COUNTRY, locale.getCountry());
    }

    private Settings() {
        this.preferences = Preferences.userRoot();
    }
}
