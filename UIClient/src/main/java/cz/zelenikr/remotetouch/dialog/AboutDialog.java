package cz.zelenikr.remotetouch.dialog;

import cz.zelenikr.remotetouch.Resources;
import cz.zelenikr.remotetouch.manager.SettingsManager;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.util.ResourceBundle;

/**
 * @author Roman Zelenik
 */
public class AboutDialog {

    private static final ResourceBundle STRINGS = Resources.loadStrings(SettingsManager.getLocale());

    private final Alert dialog;

    public AboutDialog(String title) {
        dialog = new Alert(Alert.AlertType.INFORMATION, STRINGS.getString(Resources.Strings.ABOUT_CONTENT), ButtonTypes.CLOSE);
        dialog.setHeaderText(STRINGS.getString(Resources.Strings.ABOUT_HEADER));
        setIcon(Resources.Icons.getApplicationIcon());
        setTitle(title);
    }

    /**
     * Shows the dialog and waits for the user response
     * (in other words, brings up a blocking dialog,
     * with the returned value the users input).
     * This method must be called on the JavaFX Application thread.
     * Additionally, it must either be called from an input event handler
     * or from the run method of a Runnable passed to Platform.runLater.
     * It must not be called during animation or layout processing.
     */
    public void showAndWait() {
        dialog.showAndWait();
    }

    /**
     * Changes the Icon next to Title of the dialog.
     *
     * @param icon the Icon of the dialog
     */
    public void setIcon(@NotNull Image icon) {
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(icon);
    }

    /**
     * Changes the Title of the dialog.
     *
     * @param title the Title of the dialog
     */
    public void setTitle(String title) {
        dialog.setTitle(title);
    }
}
