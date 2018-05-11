package cz.zelenikr.remotetouch.dialog;

import cz.zelenikr.remotetouch.Resources;
import cz.zelenikr.remotetouch.manager.SettingsManager;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;

import java.util.ResourceBundle;

/**
 * This class contains own localized buttons.
 *
 * @author Roman Zelenik
 */
public final class ButtonTypes {

    private static final ResourceBundle STRINGS = Resources.loadStrings(SettingsManager.getLocale());

    public static final javafx.scene.control.ButtonType
            /**
             * A pre-defined Cancel {@link ButtonType} with a
             * {@link ButtonData} of {@link ButtonData#CANCEL_CLOSE}.
             */
            CANCEL = new javafx.scene.control.ButtonType(getString(Resources.Strings.DIALOG_BUTTON_CANCEL), ButtonData.CANCEL_CLOSE),
    /**
     * A pre-defined Close {@link ButtonType} with a
     * {@link ButtonData} of {@link ButtonData#CANCEL_CLOSE}.
     */
    CLOSE = new javafx.scene.control.ButtonType(getString(Resources.Strings.DIALOG_BUTTON_CLOSE), ButtonData.CANCEL_CLOSE);


    private static String getString(String key) {
        return STRINGS.getString(key);
    }

    private ButtonTypes() {
    }
}
