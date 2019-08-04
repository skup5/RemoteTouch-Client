package cz.zelenikr.remotetouch.dialog;

import cz.zelenikr.remotetouch.Resources;
import cz.zelenikr.remotetouch.manager.SettingsManager;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;

import java.util.ResourceBundle;

/**
 * @author Roman Zelenik
 */
public class LocalizedWizardPane extends WizardPane {

    protected static ResourceBundle getStrings() {
        return Resources.INSTANCE.loadStrings(SettingsManager.getLocale());
    }

    @Override
    public void onEnteringPage(Wizard wizard) {
        ResourceBundle strings = getStrings();
        Button button = (Button) lookupButton(ButtonType.FINISH);
        if (button != null) {
            button.setText(strings.getString(Resources.Strings.DIALOG_BUTTON_FINISH));
        }
        button = (Button) lookupButton(ButtonType.CANCEL);
        if (button != null) {
            button.setText(strings.getString(Resources.Strings.DIALOG_BUTTON_CANCEL));
        }
    }

}
