package cz.zelenikr.remotetouch.dialog;


import cz.zelenikr.remotetouch.Resources;
import cz.zelenikr.remotetouch.manager.SettingsManager;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import impl.org.controlsfx.skin.DecorationPane;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * @author Roman Zelenik
 */
public class LoginDialog extends Dialog<ButtonType> {

    private static final ResourceBundle STRINGS = Resources.getStrings(SettingsManager.getLocale());

    public static final ButtonType
            BUTTON_LOGIN = new ButtonType(STRINGS.getString(Resources.Strings.LOGIN_BUTTON_LOGIN), ButtonBar.ButtonData.OK_DONE),
            BUTTON_CANCEL = new ButtonType(STRINGS.getString(Resources.Strings.DIALOG_BUTTON_CANCEL), ButtonBar.ButtonData.CANCEL_CLOSE);

    private final ValidationSupport validationSupport = new ValidationSupport();
    private final BooleanProperty invalidProperty = new SimpleBooleanProperty(false);
    private final StringProperty passwordProperty = new SimpleStringProperty();

    public LoginDialog(String title) {
        invalidProperty.bind(validationSupport.invalidProperty());
        setTitle(title);
        setHeaderText(STRINGS.getString(Resources.Strings.LOGIN_HEADER));
        setGraphic(new FontAwesomeIconView(FontAwesomeIcon.LOCK,"30"));
        getDialogPane().setContent(createContent());
        getDialogPane().getButtonTypes().addAll(BUTTON_LOGIN, BUTTON_CANCEL);
        prepareContentControls();
        Button loginBt = (Button) getDialogPane().lookupButton(BUTTON_LOGIN);
        if (loginBt != null) {
            loginBt.disableProperty().bind(invalidProperty);
        }
    }

    public String getPassword() {
        return passwordProperty.get();
    }

    private void prepareContentControls() {
        PasswordField passwordField = (PasswordField) getDialogPane().lookup("#password");
        if (passwordField != null) {
            validationSupport.registerValidator(passwordField, Validator.createEmptyValidator(STRINGS.getString(Resources.Strings.VALIDATION_LOGIN_PASSWORD)));
            passwordProperty.bind(passwordField.textProperty());
        }
    }

    private Node createContent() {
        try {
            Node view = FXMLLoader.load(ClassLoader.getSystemResource("view/login.fxml"), STRINGS);
            DecorationPane content = new DecorationPane();
            content.getChildren().add(view);
            return content;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
