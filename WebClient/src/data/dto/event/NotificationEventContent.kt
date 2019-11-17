package data.dto.event

/**
 * Represents Notification.
 *
 * @author Roman Zelenik
 */
data class NotificationEventContent(
        /**
         *  Application's package name of the current Notification.
         */
        val app: String,
        /**
         * Application's label of current Notification. Attribute of &lt;application&gt; tag in AndroidManifest file.
         */
        val label: String,
        /**
         * Title of the current Notification.
         */
        val title: String,
        /**
         * Text of the current Notification.
         */
        val text: String,
        /**
         *  A timestamp related to the current Notification, in milliseconds since the epoch.
         */
        val time: Long
) : EventContent
