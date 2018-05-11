package cz.zelenikr.remotetouch.dialog;

import cz.zelenikr.remotetouch.Resources;
import cz.zelenikr.remotetouch.manager.SettingsManager;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.ResourceBundle;

/**
 * @author Roman Zelenik
 */
public class AboutDialog {

    private static final ResourceBundle STRINGS = Resources.loadStrings(SettingsManager.getLocale());

    private final Alert dialog;

    public AboutDialog(String title) {
        dialog = new Alert(Alert.AlertType.INFORMATION, STRINGS.getString(Resources.Strings.ABOUT_CONTENT), ButtonType.CLOSE);
        dialog.setHeaderText(STRINGS.getString(Resources.Strings.ABOUT_HEADER));
        setTitle(title);
    }

    public void showAndWait(){
        dialog.showAndWait();
    }

    public void setTitle(String title) {
        dialog.setTitle(title);
    }
}
