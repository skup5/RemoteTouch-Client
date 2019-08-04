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
import javafx.util.Callback;
import org.controlsfx.validation.ValidationSupport;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * This dialog is used to user authentication.
 * The user has to be registered like the owner via {@link RegisterDialog}
 * to successful authentication.
 *
 * @author Roman Zelenik
 * @see SecurityManager
 */
public class LoginDialog extends Dialog<ButtonType> {

    private static final ResourceBundle STRINGS = Resources.INSTANCE.loadStrings(SettingsManager.getLocale());

    public static final ButtonType
            BUTTON_LOGIN = new ButtonType(STRINGS.getString(Resources.Strings.LOGIN_BUTTON_LOGIN), ButtonBar.ButtonData.FINISH),
            BUTTON_RESET = new ButtonType(STRINGS.getString(Resources.Strings.LOGIN_BUTTON_RESET), ButtonBar.ButtonData.OTHER);

    private final ValidationSupport validationSupport = new ValidationSupport();
    private final BooleanProperty invalidProperty = new SimpleBooleanProperty(false);
    private final StringProperty passwordProperty = new SimpleStringProperty();
    private Callback<UserInfo, Boolean> onAuthenticateCallback;
    private boolean authenticated = false;

    public LoginDialog(String title) {
        invalidProperty.bind(validationSupport.invalidProperty());
        setIcon(Resources.Icons.INSTANCE.getApplicationIcon());
        setTitle(title);
        setHeaderText(STRINGS.getString(Resources.Strings.LOGIN_HEADER));
        setGraphic(new FontAwesomeIconView(FontAwesomeIcon.LOCK, "30"));
        getDialogPane().setContent(createContent());
        getDialogPane().getButtonTypes().addAll(BUTTON_RESET, BUTTON_LOGIN, ButtonTypes.CANCEL);
        prepareContentControls();
        Button loginBt = (Button) getDialogPane().lookupButton(BUTTON_LOGIN);
        if (loginBt != null) {
            loginBt.disableProperty().bind(invalidProperty);
            loginBt.setOnAction(event -> authenticate());
        }
    }

    /**
     * @return {@link UserInfo} object created from user input values.
     */
    public UserInfo getUser() {
        return new UserInfo(passwordProperty.get());
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setOnAuthenticateCallback(Callback<UserInfo, Boolean> onAuthenticateCallback) {
        this.onAuthenticateCallback = onAuthenticateCallback;
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

    private boolean authenticate() {
        //System.out.println("authenticate()");
        if (onAuthenticateCallback != null) {
            authenticated = onAuthenticateCallback.call(getUser());
        }
        return authenticated;
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
            Node view = Resources.INSTANCE.loadView("view/login.fxml", STRINGS).getKey();
            DecorationPane content = new DecorationPane();
            content.getChildren().add(view);
            return content;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
