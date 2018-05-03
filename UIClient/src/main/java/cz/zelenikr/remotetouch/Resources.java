package cz.zelenikr.remotetouch;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * @author Roman Zelenik
 */
public final class Resources {

    /**
     * Returns a string resource bundle for the given Locale in locales.
     *
     * @param locale the locale for which a resource bundle is desired
     * @return resource bundle with strings
     */
    public static ResourceBundle getStrings(@NotNull Locale locale) {
        return ResourceBundle.getBundle("values/strings", locale, new UTF8Control());
    }

    public static String[] getStyleSheets() {
        return new String[]{"css/style.css"};
    }

    /**
     * Contains keys of string resources.
     */
    public static final class Strings {
        public static final String
                APPLICATION_TITLE = "Application.Title",

                CALLTYPE_ENDED = "CallType.Ended",
                CALLTYPE_MISSED = "CallType.Missed",
                CALLTYPE_ONGOING = "CallType.Ongoing",
                CALLTYPE_INCOMING = "CallType.Incoming",
                CALLTYPE_OUTGOING = "CallType.Outgoing",

                CONNECTSTATUS_CONNECTED = "ConnectionStatus.Connected",
                CONNECTSTATUS_CONNECTING = "ConnectionStatus.Connecting",
                CONNECTSTATUS_DISCONNECTED = "ConnectionStatus.Disconnected",
                CONNECTSTATUS_RECONNECTED = "ConnectionStatus.Reconnected",
                CONNECTSTATUS_RECONNECTING = "ConnectionStatus.Reconnecting",
                CONNECTSTATUS_CONNECT_ERROR = "ConnectionStatus.Connect.Error",
                CONNECTSTATUS_RECONNECT_ERROR = "ConnectionStatus.Reconnect.Error",

                DIALOG_BUTTON_CANCEL = "Dialog.Button.Cancel",
                DIALOG_BUTTON_FINISH = "Dialog.Button.Finish",

                LOGIN_BUTTON_LOGIN = "Login.Button.Login",
                LOGIN_HEADER = "Login.Header",

                NAVIGATION_ITEMS_PAIR = "Navigation.Items.Pair",
                NAVIGATION_ITEMS_SETTINGS = "Navigation.Items.Settings",
                NAVIGATION_ITEMS_MESSAGES = "Navigation.Items.Messages",
                NAVIGATION_ITEMS_CALLS = "Navigation.Items.Calls",

                VALIDATION_DEVICE_NAME = "Validation.Device.Name",
                VALIDATION_DEVICE_PAIR_KEY = "Validation.Device.PairKey",
                VALIDATION_CONNECTION_ADDRESS = "Validation.Connection.Address",
                VALIDATION_LOGIN_PASSWORD = "Validation.Login.Password",

                SETTINGS_DEVICE_NAME = "Settings.Device.Name",
                SETTINGS_DEVICE_PAIR_KEY = "Settings.Device.PairKey",

                WIZARD_PAIR_DEVICE_HEADER = "Wizard.PairDevice.Header";
    }

    private Resources() {
    }

    private static class UTF8Control extends ResourceBundle.Control {
        public ResourceBundle newBundle
                (String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
                throws IllegalAccessException, InstantiationException, IOException {
            // The below is a copy of the default implementation.
            String bundleName = toBundleName(baseName, locale);
            String resourceName = toResourceName(bundleName, "properties");
            ResourceBundle bundle = null;
            InputStream stream = null;
            if (reload) {
                URL url = loader.getResource(resourceName);
                if (url != null) {
                    URLConnection connection = url.openConnection();
                    if (connection != null) {
                        connection.setUseCaches(false);
                        stream = connection.getInputStream();
                    }
                }
            } else {
                stream = loader.getResourceAsStream(resourceName);
            }
            if (stream != null) {
                try {
                    // Only this line is changed to make it to read properties files as UTF-8.
                    bundle = new PropertyResourceBundle(new InputStreamReader(stream, "UTF-8"));
                } finally {
                    stream.close();
                }
            }
            return bundle;
        }
    }
}
