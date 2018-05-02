package cz.zelenikr.remotetouch.data.mapper;

import cz.zelenikr.remotetouch.Resources;
import cz.zelenikr.remotetouch.manager.SettingsManager;
import cz.zelenikr.remotetouch.data.dto.CallType;

import java.util.ResourceBundle;

/**
 * This helper class converts {@link CallType} values into the localized strings.
 *
 * @author Roman Zelenik
 */
public final class CallTypeToLocalStringMapper {

    private static final ResourceBundle STRINGS = Resources.getStrings(SettingsManager.getLocale());

    public static String toString(CallType value) {
        switch (value) {
            case ENDED:
                return STRINGS.getString(Resources.Strings.CALLTYPE_ENDED);
            case MISSED:
                return STRINGS.getString(Resources.Strings.CALLTYPE_MISSED);
            case ONGOING:
                return STRINGS.getString(Resources.Strings.CALLTYPE_ONGOING);
            case INCOMING:
                return STRINGS.getString(Resources.Strings.CALLTYPE_INCOMING);
            case OUTGOING:
                return STRINGS.getString(Resources.Strings.CALLTYPE_OUTGOING);
            default:
                return value.toString();
        }
    }

    private CallTypeToLocalStringMapper() {
    }
}
