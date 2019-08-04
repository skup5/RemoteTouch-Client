package cz.zelenikr.remotetouch.dialog;

import cz.zelenikr.remotetouch.Resources;
import cz.zelenikr.remotetouch.manager.SettingsManager;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.util.ResourceBundle;

/**
 * @author Roman Zelenik
 */
public class ErrorDialog {

    private static final ResourceBundle STRINGS = Resources.INSTANCE.loadStrings(SettingsManager.getLocale());

    private final Alert dialog;

    public ErrorDialog(String title, String content) {
        dialog = new Alert(Alert.AlertType.ERROR, content, ButtonType.OK);
        setTitle(title);
        setIcon(Resources.Icons.INSTANCE.getApplicationIcon());
        setHeaderText(STRINGS.getString(Resources.Strings.DIALOG_HEADER_ERROR));
    }

    /**
     * Sets the string to show in the dialog header area.
     * Note that the header text is lower precedence than the header node,
     * meaning that if both the header node and the headerText properties are set,
     * the header text will not be displayed in a default DialogPane instance.
     *
     * @param header the string to show in the dialog header area
     */
    public void setHeaderText(String header) {
        dialog.setHeaderText(header);
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
}
