package cz.zelenikr.remotetouch.dialog;

import cz.zelenikr.remotetouch.Resources;
import cz.zelenikr.remotetouch.data.dto.UserInfo;
import cz.zelenikr.remotetouch.manager.SettingsManager;
import cz.zelenikr.remotetouch.validation.Validators;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import impl.org.controlsfx.skin.DecorationPane;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.controlsfx.validation.ValidationSupport;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * This dialog is used to registration new app owner.
 * The owner can be later used to authentication via {@link LoginDialog}.
 *
 * @author Roman Zelenik
 * @see SecurityManager
 */
public class RegisterDialog extends Dialog<ButtonType> {

    private static final ResourceBundle STRINGS = Resources.INSTANCE.loadStrings(SettingsManager.getLocale());

    public static final ButtonType
            BUTTON_CREATE = new ButtonType(STRINGS.getString(Resources.Strings.REGISTER_BUTTON_CREATE), ButtonBar.ButtonData.FINISH);

    private final ValidationSupport validationSupport = new ValidationSupport();
    private final BooleanProperty invalidProperty = new SimpleBooleanProperty(false);
    private final StringProperty passwordProperty = new SimpleStringProperty();

    public RegisterDialog(String title) {
        invalidProperty.bind(validationSupport.invalidProperty());
        setIcon(Resources.Icons.INSTANCE.getApplicationIcon());
        setTitle(title);
        setHeaderText(STRINGS.getString(Resources.Strings.REGISTER_HEADER));
        setGraphic(new FontAwesomeIconView(FontAwesomeIcon.LOCK, "30"));
        getDialogPane().setContent(createContent());
        getDialogPane().getButtonTypes().addAll(BUTTON_CREATE, ButtonTypes.CANCEL);
        prepareContentControls();
        Button registerBt = (Button) getDialogPane().lookupButton(BUTTON_CREATE);
        if (registerBt != null) {
            registerBt.disableProperty().bind(invalidProperty);
        }
    }

    public UserInfo getUser() {
        return new UserInfo(passwordProperty.get());
    }

    /**
     * Changes the Icon next to Title of the dialog.
     *
     * @param icon the Icon of the dialog
     */
    public void setIcon(@NotNull Image icon) {
        Stage stage = (Stage) getDialogPane().getScene().getWindow();
        stage.getIcons().add(icon);
    }

    private void prepareContentControls() {
        PasswordField passwordField = (PasswordField) getDialogPane().lookup("#password");
        if (passwordField != null) {
            validationSupport.registerValidator(passwordField, Validators.createPasswordValidator(STRINGS.getString(Resources.Strings.VALIDATION_REGISTER_PASSWORD)));
            passwordProperty.bind(passwordField.textProperty());
        }
    }

    private Node createContent() {
        try {
            Node view = Resources.INSTANCE.loadView("view/register.fxml", STRINGS).getKey();
            DecorationPane content = new DecorationPane();
            content.getChildren().add(view);
            return content;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
