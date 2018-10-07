package cz.zelenikr.remotetouch;

import cz.zelenikr.remotetouch.controller.Controller;
import cz.zelenikr.remotetouch.data.dto.CallType;
import cz.zelenikr.remotetouch.network.ConnectionStatus;
import de.jensd.fx.glyphs.GlyphIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
     * Loads and returns a string resource bundle for the given Locale in locales.
     *
     * @param locale the locale for which a resource bundle is desired
     * @return resource bundle with strings
     */
    public static ResourceBundle loadStrings(@NotNull Locale locale) {
        return ResourceBundle.getBundle("values/strings", locale, new UTF8Control());
    }

    public static String[] getStyleSheets() {
        return new String[]{"css/style.css"};
    }

    /**
     * Loads and returns FXML view with the specific name. If some controller is specified,
     * returns the controller associated with the root object too.
     *
     * @param name      a view resource path
     * @param resources the resources used to resolve resource key attribute values
     * @return a view with the given resource name and its {@link Controller}
     * @throws IOException if an error occurs during loading
     */
    public static Pair<Node, Controller> loadView(@NotNull String name, @Nullable ResourceBundle resources) throws IOException {
        FXMLLoader loader;
        if (resources == null) loader = new FXMLLoader(ClassLoader.getSystemResource(name));
        else loader = new FXMLLoader(ClassLoader.getSystemResource(name), resources);

        Node view = loader.load();
        Controller controller = loader.getController();
        return new Pair<>(view, controller);
    }

    /**
     * Loads FXML view with the specific name and sets the specific {@link Controller} to that view.
     *
     * @param name       a view resource path
     * @param controller the given controller
     * @param resources  the resources used to resolve resource key attribute values
     * @return a view with the given resource name and {@link Controller}
     * @throws IOException if an error occurs during loading
     */
    public static Node loadView(@NotNull String name, @NotNull Controller controller, @Nullable ResourceBundle resources) throws IOException {
        FXMLLoader loader;
        if (resources == null) loader = new FXMLLoader(ClassLoader.getSystemResource(name));
        else loader = new FXMLLoader(ClassLoader.getSystemResource(name), resources);

        loader.setController(controller);
        return loader.load();
    }

    /**
     * Finds a resource with a given name.
     *
     * @param name path to the file in {@code resources}
     * @return A InputStream object or null if no resource with this name is found.
     */
    public static InputStream loadRaw(@NotNull String name) {
        return Resources.class.getResourceAsStream(name);
    }

    /**
     * Contains keys of string resources.
     */
    public static final class Strings {
        public static final String
                ABOUT_CONTENT = "About.Content",
                ABOUT_HEADER = "About.Header",

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
                DIALOG_BUTTON_CLOSE = "Dialog.Button.Close",
                DIALOG_BUTTON_FINISH = "Dialog.Button.Finish",
                DIALOG_HEADER_ERROR = "Dialog.Header.Error",

        LOGIN_BUTTON_LOGIN = "Login.Button.Login",
                LOGIN_BUTTON_RESET = "Login.Button.Reset",
                LOGIN_HEADER = "Login.Header",
                LOGIN_ERROR_CONTENT = "Login.Error.Content",
                LOGIN_ERROR_HEADER = "Login.Error.Header",

        NAVIGATION_ITEMS_PAIR = "Navigation.Items.Pair",
                NAVIGATION_ITEMS_SETTINGS = "Navigation.Items.Settings",
                NAVIGATION_ITEMS_MESSAGES = "Navigation.Items.Messages",
                NAVIGATION_ITEMS_CALLS = "Navigation.Items.Calls",

        NOTIFICATION_TITLE_CALL = "Notification.Title.Call",
                NOTIFICATION_TITLE_NOTIFICATION = "Notification.Title.Notification",
                NOTIFICATION_TITLE_SMS = "Notification.Title.Sms",

        REGISTER_BUTTON_CREATE = "Register.Button.Create",
                REGISTER_HEADER = "Register.Header",

        SETTINGS_DEVICE_NAME = "Settings.Device.Name",
                SETTINGS_DEVICE_PAIR_KEY = "Settings.Device.PairKey",

        VALIDATION_DEVICE_NAME = "Validation.Device.Name",
                VALIDATION_DEVICE_PAIR_KEY = "Validation.Device.PairKey",
                VALIDATION_CONNECTION_ADDRESS = "Validation.Connection.Address",
                VALIDATION_REGISTER_PASSWORD = "Validation.Register.Password",

        WIZARD_PAIR_DEVICE_HEADER = "Wizard.PairDevice.Header";

    }

    /**
     * Contains all glyph icons that are used (programmatically) in application.
     */
    public static final class Icons {

        /**
         * Creates and returns icon of this JavaFX application.
         *
         * @return
         */
        public static Image getApplicationIcon() {
            return new Image(Resources.class.getResourceAsStream("/icon.png"));
        }

        /**
         * Selects icon for the specific mobile app or returns default app icon.<p/>
         * For Android applications, use the app package name.
         *
         * @param app the given name of application
         * @return
         */
        public static GlyphIcon getIconByApp(String app) {
            FontAwesomeIcon glyph = null;
            if (app.contains("whatsapp")) glyph = FontAwesomeIcon.WHATSAPP;

            else if (app.contains("youtube")) glyph = FontAwesomeIcon.YOUTUBE;
            else if (app.contains("spotify")) glyph = FontAwesomeIcon.SPOTIFY;
            else if (app.contains("soundcloud")) glyph = FontAwesomeIcon.SOUNDCLOUD;

            else if (app.contains("facebook")) glyph = FontAwesomeIcon.FACEBOOK_OFFICIAL;
            else if (app.contains("instagram")) glyph = FontAwesomeIcon.INSTAGRAM;
//            else if (app.contains("snapchat")) glyph = FontAwesomeIcon.SNAPCHAT;
            else if (app.contains("twitter")) glyph = FontAwesomeIcon.TWITTER;
            else if (app.contains("linkedin")) glyph = FontAwesomeIcon.LINKEDIN;

            else if (app.contains("amazon")) glyph = FontAwesomeIcon.AMAZON;

            else if (app.contains("dropbox")) glyph = FontAwesomeIcon.DROPBOX;
            else if (app.contains("flickr")) glyph = FontAwesomeIcon.FLICKR;

            else if (app.contains("wikipedia")) glyph = FontAwesomeIcon.WIKIPEDIA_W;
            else if (app.contains("reddit")) glyph = FontAwesomeIcon.REDDIT;
            else if (app.contains("stackexchange ")) glyph = FontAwesomeIcon.STACK_OVERFLOW;
            else if (app.contains("stackoverflow")) glyph = FontAwesomeIcon.STACK_OVERFLOW;

            else if (app.contains("github")) glyph = FontAwesomeIcon.GITHUB;
            else if (app.contains("bitbucket")) glyph = FontAwesomeIcon.BITBUCKET;
//            else if (app.contains("gitlab")) glyph = FontAwesomeIcon.GITLAB;
            else if (app.contains("git")) glyph = FontAwesomeIcon.GIT;
            else if (app.contains("slack")) glyph = FontAwesomeIcon.SLACK;

            else if (app.contains("chrome")) glyph = FontAwesomeIcon.CHROME;
            else if (app.contains("firefox")) glyph = FontAwesomeIcon.FIREFOX;
            else if (app.contains("opera")) glyph = FontAwesomeIcon.OPERA;
            else if (app.contains("yahoo")) glyph = FontAwesomeIcon.YAHOO;

            else if (app.contains("microsoft")) glyph = FontAwesomeIcon.WINDOWS;
            else if (app.contains("windows")) glyph = FontAwesomeIcon.WINDOWS;
            else if (app.contains("bluetooth")) glyph = FontAwesomeIcon.BLUETOOTH;
            else if (app.contains("google")) glyph = FontAwesomeIcon.GOOGLE;


            else if (app.contains("mail")) glyph = FontAwesomeIcon.ENVELOPE;
            else if (app.contains("calendar")) glyph = FontAwesomeIcon.CALENDAR;
            else if (app.contains("com.android")) glyph = FontAwesomeIcon.ANDROID;

            return glyph == null ? new MaterialDesignIconView(MaterialDesignIcon.APPS) : new FontAwesomeIconView(glyph);
        }

        public static GlyphIcon getIconByCallType(CallType type) {
            MaterialDesignIcon glyph;
            switch (type) {
                case ENDED:
                    glyph = MaterialDesignIcon.PHONE_HANGUP;
                    break;
                case INCOMING:
                    glyph = MaterialDesignIcon.PHONE_INCOMING;
                    break;
                case MISSED:
                    glyph = MaterialDesignIcon.PHONE_MISSED;
                    break;
                case ONGOING:
                    glyph = MaterialDesignIcon.PHONE_IN_TALK;
                    break;
                case OUTGOING:
                    glyph = MaterialDesignIcon.PHONE_OUTGOING;
                    break;
                default:
                    glyph = MaterialDesignIcon.PHONE;
                    break;
            }
            return new MaterialDesignIconView(glyph);
        }

        public static GlyphIcon getSmsIcon() {
            return new MaterialDesignIconView(MaterialDesignIcon.MESSAGE_TEXT);
        }

        public static GlyphIcon getIconByConnectionStatus(ConnectionStatus status) {
            MaterialDesignIcon glyph;
            switch (status) {
                case CONNECTING:
                case RECONNECTING:
                    glyph = MaterialDesignIcon.SWAP_HORIZONTAL;
                    break;

                case CONNECTED:
                case RECONNECTED:
                    glyph = MaterialDesignIcon.SYNC;
                    break;

                case CONNECT_ERROR:
                case RECONNECT_ERROR:
                    glyph = MaterialDesignIcon.SYNC_ALERT;
                    break;

                case DISCONNECTED:
                    glyph = MaterialDesignIcon.SYNC_OFF;
                    break;

                default:
                    glyph = MaterialDesignIcon.SYNC_ALERT;

            }
            return new MaterialDesignIconView(glyph);
        }

        public static GlyphIcon getRemoveEventIcon() {
            return new MaterialDesignIconView(MaterialDesignIcon.CLOSE);
        }
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
