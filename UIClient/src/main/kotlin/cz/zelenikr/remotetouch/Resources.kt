package cz.zelenikr.remotetouch

import cz.zelenikr.remotetouch.controller.Controller
import cz.zelenikr.remotetouch.data.dto.CallType
import cz.zelenikr.remotetouch.network.ConnectionStatus
import de.jensd.fx.glyphs.GlyphIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.image.Image
import javafx.util.Pair
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.*

/**
 * @author Roman Zelenik
 */
object Resources {

    val styleSheets: Array<String>
        get() = arrayOf("css/style.css")

    /**
     * Loads and returns a string resource bundle for the given Locale in locales.
     *
     * @param locale the locale for which a resource bundle is desired
     * @return resource bundle with strings
     */
    fun loadStrings(locale: Locale): ResourceBundle {
        return ResourceBundle.getBundle("values/strings", locale, UTF8Control())
    }

    /**
     * Loads and returns FXML view with the specific name. If some controller is specified,
     * returns the controller associated with the root object too.
     *
     * @param name      a view resource path
     * @param resources the resources used to resolve resource key attribute values
     * @return a view with the given resource name and its [Controller]
     * @throws IOException if an error occurs during loading
     */
    @Throws(IOException::class)
    fun loadView(name: String, resources: ResourceBundle?): Pair<Node, Controller> {
        val loader: FXMLLoader = if (resources == null)
            FXMLLoader(ClassLoader.getSystemResource(name))
        else
            FXMLLoader(ClassLoader.getSystemResource(name), resources)

        val view = loader.load<Node>()
        val controller = loader.getController<Controller>()
        return Pair(view, controller)
    }

    /**
     * Loads FXML view with the specific name and sets the specific [Controller] to that view.
     *
     * @param name       a view resource path
     * @param controller the given controller
     * @param resources  the resources used to resolve resource key attribute values
     * @return a view with the given resource name and [Controller]
     * @throws IOException if an error occurs during loading
     */
    @Throws(IOException::class)
    fun loadView(name: String, controller: Controller, resources: ResourceBundle?): Node {
        val loader: FXMLLoader = if (resources == null)
            FXMLLoader(ClassLoader.getSystemResource(name))
        else
            FXMLLoader(ClassLoader.getSystemResource(name), resources)

        loader.setController(controller)
        return loader.load()
    }

    /**
     * Finds a resource with a given name.
     *
     * @param name path to the file in `resources`
     * @return A InputStream object or null if no resource with this name is found.
     */
    fun loadRaw(name: String): InputStream {
        return Resources::class.java.getResourceAsStream(name)
    }

    /**
     * Contains keys of string resources.
     */
    object Strings {
        const val ABOUT_CONTENT = "About.Content"
        const val ABOUT_HEADER = "About.Header"

        const val APPLICATION_TITLE = "Application.Title"

        const val CALLTYPE_ENDED = "CallType.Ended"
        const val CALLTYPE_MISSED = "CallType.Missed"
        const val CALLTYPE_ONGOING = "CallType.Ongoing"
        const val CALLTYPE_INCOMING = "CallType.Incoming"
        const val CALLTYPE_OUTGOING = "CallType.Outgoing"

        const val CONNECTSTATUS_CONNECTED = "ConnectionStatus.Connected"
        const val CONNECTSTATUS_CONNECTING = "ConnectionStatus.Connecting"
        const val CONNECTSTATUS_DISCONNECTED = "ConnectionStatus.Disconnected"
        const val CONNECTSTATUS_RECONNECTED = "ConnectionStatus.Reconnected"
        const val CONNECTSTATUS_RECONNECTING = "ConnectionStatus.Reconnecting"
        const val CONNECTSTATUS_CONNECT_ERROR = "ConnectionStatus.Connect.Error"
        const val CONNECTSTATUS_RECONNECT_ERROR = "ConnectionStatus.Reconnect.Error"

        const val DIALOG_BUTTON_CANCEL = "Dialog.Button.Cancel"
        const val DIALOG_BUTTON_CLOSE = "Dialog.Button.Close"
        const val DIALOG_BUTTON_FINISH = "Dialog.Button.Finish"
        const val DIALOG_HEADER_ERROR = "Dialog.Header.Error"

        const val LOGIN_BUTTON_LOGIN = "Login.Button.Login"
        const val LOGIN_BUTTON_RESET = "Login.Button.Reset"
        const val LOGIN_HEADER = "Login.Header"
        const val LOGIN_ERROR_CONTENT = "Login.Error.Content"
        const val LOGIN_ERROR_HEADER = "Login.Error.Header"

        const val NAVIGATION_ITEMS_PAIR = "Navigation.Items.Pair"
        const val NAVIGATION_ITEMS_SETTINGS = "Navigation.Items.Settings"
        const val NAVIGATION_ITEMS_MESSAGES = "Navigation.Items.Messages"
        const val NAVIGATION_ITEMS_CALLS = "Navigation.Items.Calls"

        const val NOTIFICATION_TITLE_CALL = "Notification.Title.Call"
        const val NOTIFICATION_TITLE_NOTIFICATION = "Notification.Title.Notification"
        const val NOTIFICATION_TITLE_SMS = "Notification.Title.Sms"

        const val REGISTER_BUTTON_CREATE = "Register.Button.Create"
        const val REGISTER_HEADER = "Register.Header"

        const val SETTINGS_DEVICE_NAME = "Settings.Device.Name"
        const val SETTINGS_DEVICE_PAIR_KEY = "Settings.Device.PairKey"

        const val VALIDATION_DEVICE_NAME = "Validation.Device.Name"
        const val VALIDATION_DEVICE_PAIR_KEY = "Validation.Device.PairKey"
        const val VALIDATION_CONNECTION_ADDRESS = "Validation.Connection.Address"
        const val VALIDATION_REGISTER_PASSWORD = "Validation.Register.Password"

        const val WIZARD_PAIR_DEVICE_HEADER = "Wizard.PairDevice.Header"

    }

    /**
     * Contains all glyph icons that are used (programmatically) in application.
     */
    object Icons {

        /**
         * Creates and returns icon of this JavaFX application.
         *
         * @return
         */
        val applicationIcon: Image
            get() = Image(Resources::class.java.getResourceAsStream("/icon.png"))

        val smsIcon: GlyphIcon<*>
            get() = MaterialIconView(MaterialIcon.TEXTSMS)

        val removeEventIcon: GlyphIcon<*>
            get() = MaterialIconView(MaterialIcon.CLOSE)

        /**
         * Selects icon for the specific mobile app or returns default app icon.
         *
         *
         * For Android applications, use the app package name.
         *
         * @param app the given name of application
         * @return
         */
        fun getIconByApp(app: String): GlyphIcon<*> {
            var glyph: FontAwesomeIcon? = null
            when {
                app.contains("whatsapp") -> glyph = FontAwesomeIcon.WHATSAPP
                app.contains("youtube") -> glyph = FontAwesomeIcon.YOUTUBE
                app.contains("spotify") -> glyph = FontAwesomeIcon.SPOTIFY
                app.contains("soundcloud") -> glyph = FontAwesomeIcon.SOUNDCLOUD
                app.contains("facebook") -> glyph = FontAwesomeIcon.FACEBOOK_OFFICIAL
                app.contains("instagram") -> glyph = FontAwesomeIcon.INSTAGRAM
                app.contains("snapchat") -> glyph = FontAwesomeIcon.SNAPCHAT
                app.contains("twitter") -> glyph = FontAwesomeIcon.TWITTER
                app.contains("linkedin") -> glyph = FontAwesomeIcon.LINKEDIN
                app.contains("amazon") -> glyph = FontAwesomeIcon.AMAZON
                app.contains("dropbox") -> glyph = FontAwesomeIcon.DROPBOX
                app.contains("flickr") -> glyph = FontAwesomeIcon.FLICKR
                app.contains("wikipedia") -> glyph = FontAwesomeIcon.WIKIPEDIA_W
                app.contains("reddit") -> glyph = FontAwesomeIcon.REDDIT
                app.contains("stackexchange ") -> glyph = FontAwesomeIcon.STACK_OVERFLOW
                app.contains("stackoverflow") -> glyph = FontAwesomeIcon.STACK_OVERFLOW
                app.contains("github") -> glyph = FontAwesomeIcon.GITHUB
                app.contains("bitbucket") -> glyph = FontAwesomeIcon.BITBUCKET
                app.contains("gitlab") -> glyph = FontAwesomeIcon.GITLAB
                app.contains("git") -> glyph = FontAwesomeIcon.GIT
                app.contains("slack") -> glyph = FontAwesomeIcon.SLACK
                app.contains("chrome") -> glyph = FontAwesomeIcon.CHROME
                app.contains("firefox") -> glyph = FontAwesomeIcon.FIREFOX
                app.contains("opera") -> glyph = FontAwesomeIcon.OPERA
                app.contains("yahoo") -> glyph = FontAwesomeIcon.YAHOO
                app.contains("microsoft") -> glyph = FontAwesomeIcon.WINDOWS
                app.contains("windows") -> glyph = FontAwesomeIcon.WINDOWS
                app.contains("bluetooth") -> glyph = FontAwesomeIcon.BLUETOOTH
                app.contains("google") -> glyph = FontAwesomeIcon.GOOGLE
                app.contains("mail") -> glyph = FontAwesomeIcon.ENVELOPE
                app.contains("calendar") -> glyph = FontAwesomeIcon.CALENDAR
                app.contains("com.android") -> glyph = FontAwesomeIcon.ANDROID
            }

            return glyph?.let { FontAwesomeIconView(it) } ?: MaterialIconView(MaterialIcon.APPS)
        }

        fun getIconByCallType(type: CallType): GlyphIcon<*> {
            val glyph: MaterialIcon
            when (type) {
                CallType.ENDED -> glyph = MaterialIcon.CALL_END
                CallType.INCOMING -> glyph = MaterialIcon.RING_VOLUME
                CallType.MISSED -> glyph = MaterialIcon.PHONE_MISSED
                CallType.ONGOING -> glyph = MaterialIcon.PHONE_IN_TALK
                CallType.OUTGOING -> glyph = MaterialIcon.PHONE_FORWARDED
                else -> glyph = MaterialIcon.PHONE
            }
            return MaterialIconView(glyph)
        }

        fun getIconByConnectionStatus(status: ConnectionStatus): GlyphIcon<*> {
            val glyph: MaterialIcon = when (status) {
                ConnectionStatus.CONNECTING, ConnectionStatus.RECONNECTING -> MaterialIcon.SWAP_HORIZ

                ConnectionStatus.CONNECTED, ConnectionStatus.RECONNECTED -> MaterialIcon.SYNC

                ConnectionStatus.CONNECT_ERROR, ConnectionStatus.RECONNECT_ERROR -> MaterialIcon.SYNC_PROBLEM

                ConnectionStatus.DISCONNECTED -> MaterialIcon.SYNC_DISABLED

                else -> MaterialIcon.SYNC_PROBLEM
            }
            return MaterialIconView(glyph)
        }
    }

    private class UTF8Control : ResourceBundle.Control() {
        @Throws(IllegalAccessException::class, InstantiationException::class, IOException::class)
        override fun newBundle(baseName: String, locale: Locale, format: String, loader: ClassLoader, reload: Boolean): ResourceBundle? {
            // The below is a copy of the default implementation.
            val bundleName = toBundleName(baseName, locale)
            val resourceName = toResourceName(bundleName, "properties")
            var bundle: ResourceBundle? = null
            var stream: InputStream? = null
            if (reload) {
                val url = loader.getResource(resourceName)
                if (url != null) {
                    val connection = url.openConnection()
                    if (connection != null) {
                        connection.useCaches = false
                        stream = connection.getInputStream()
                    }
                }
            } else {
                stream = loader.getResourceAsStream(resourceName)
            }
            if (stream != null) {
                try {
                    // Only this line is changed to make it to read properties files as UTF-8.
                    bundle = PropertyResourceBundle(InputStreamReader(stream, StandardCharsets.UTF_8))
                } finally {
                    stream.close()
                }
            }
            return bundle
        }
    }
}
