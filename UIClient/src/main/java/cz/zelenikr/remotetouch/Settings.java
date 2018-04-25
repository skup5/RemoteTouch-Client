package cz.zelenikr.remotetouch;

import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
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
            KEY_LOCALE_COUNTRY = "locale_country",
            KEY_DEVICE_NAME = "device_name",
            KEY_PAIR_KEY = "pair_key",
            KEY_SERVER_ADDRESS = "server_address";

    private static final String
            DEF_LOCALE_LANG = "cs",
            DEF_LOCALE_COUNTRY = "CZ",
            DEF_DEVICE_NAME = "",
            DEF_PAIR_KEY = "",
            DEF_SERVER_ADDRESS = "http://localhost:8080/socket";
//            DEF_SERVER_ADDRESS = "http://10.0.0.18:8080/socket";
//            DEF_SERVER_ADDRESS = "https://remotetouch.tk/socket";

    private static final Logger LOGGER = Logger.getLogger(Settings.class.getSimpleName());

    private static final Settings INSTANCE = new Settings();

    public static Settings getInstance() {
        return INSTANCE;
    }

    private final Preferences preferences;

    /**
     * @return true if some value (including empty string) is stored
     */
    public boolean containsDeviceName() {
        return contains(KEY_DEVICE_NAME);
    }

    public String getDeviceName() {
        return preferences.get(KEY_DEVICE_NAME, DEF_DEVICE_NAME);
    }

    /**
     * Changes device name value.
     *
     * @param deviceName if it's {@code null}, value will be reset to the default value
     */
    public void setDeviceName(String deviceName) {
        LOGGER.info("store " + deviceName);
        if (deviceName == null) {
            preferences.remove(KEY_DEVICE_NAME);
        } else {
            preferences.put(KEY_DEVICE_NAME, deviceName);
        }
    }

    /**
     * @return true if some value (including empty string) is stored
     */
    public boolean containsPairKey() {
        return contains(KEY_PAIR_KEY);
    }

    public String getPairKey() {
        return preferences.get(KEY_PAIR_KEY, DEF_PAIR_KEY);
    }

    /**
     * @param pairKey if it's {@code null}, value will be reset to the default value
     */
    public void setPairKey(String pairKey) {
        LOGGER.info("store " + pairKey);
        if (pairKey == null) {
            preferences.remove(KEY_PAIR_KEY);
        } else {
            preferences.put(KEY_PAIR_KEY, pairKey);
        }
    }

    /**
     * @param key the given key
     * @return true if preferences contains value with the specific key
     */
    private boolean contains(@NotNull String key) {
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
        String country = preferences.get(KEY_LOCALE_COUNTRY, DEF_LOCALE_COUNTRY);
        return new Locale(lang, country);
    }

    public void setLocale(@NotNull Locale locale) {
        preferences.put(KEY_LOCALE_LANG, locale.getLanguage());
        preferences.put(KEY_LOCALE_COUNTRY, locale.getCountry());
    }

    public URL getServerAddress() {
        try {
            return new URL(preferences.get(KEY_SERVER_ADDRESS, DEF_SERVER_ADDRESS));
        } catch (MalformedURLException e) {
            // This shouldn't have happened because we always store a valid URL.
            e.printStackTrace();
            return null;
        }
    }

    public void setServerAddress(@NotNull URL address) {
        LOGGER.info("store " + address.toExternalForm());
        preferences.put(KEY_SERVER_ADDRESS, address.toExternalForm());
    }

    private Settings() {
        this.preferences = Preferences.userRoot();
    }
}
