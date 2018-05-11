package cz.zelenikr.remotetouch.data.mapper;

import cz.zelenikr.remotetouch.Resources;
import cz.zelenikr.remotetouch.manager.SettingsManager;
import cz.zelenikr.remotetouch.network.ConnectionStatus;

import java.util.ResourceBundle;

/**
 * This helper class converts {@link ConnectionStatus} values into the localized strings.
 *
 * @author Roman Zelenik
 */
public class ConnectionStatusToLocaleStringMapper {

    private static final ResourceBundle STRINGS = Resources.loadStrings(SettingsManager.getLocale());

    public static String toString(ConnectionStatus value) {
        switch (value) {
            case CONNECTED:
                return STRINGS.getString(Resources.Strings.CONNECTSTATUS_CONNECTED);
            case CONNECTING:
                return STRINGS.getString(Resources.Strings.CONNECTSTATUS_CONNECTING);
            case RECONNECTED:
                return STRINGS.getString(Resources.Strings.CONNECTSTATUS_RECONNECTED);
            case RECONNECTING:
                return STRINGS.getString(Resources.Strings.CONNECTSTATUS_RECONNECTING);
            case DISCONNECTED:
                return STRINGS.getString(Resources.Strings.CONNECTSTATUS_DISCONNECTED);
            case CONNECT_ERROR:
                return STRINGS.getString(Resources.Strings.CONNECTSTATUS_CONNECT_ERROR);
            case RECONNECT_ERROR:
                return STRINGS.getString(Resources.Strings.CONNECTSTATUS_RECONNECT_ERROR);
            default:
                return value.toString();
        }
    }

    private ConnectionStatusToLocaleStringMapper() {
    }
}
