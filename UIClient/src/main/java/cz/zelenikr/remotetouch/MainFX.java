package cz.zelenikr.remotetouch;


import cz.zelenikr.remotetouch.controller.AppController;
import cz.zelenikr.remotetouch.controller.Controller;
import cz.zelenikr.remotetouch.controller.settings.PairDeviceController;
import cz.zelenikr.remotetouch.data.dto.UserInfo;
import cz.zelenikr.remotetouch.dialog.LocalizedWizardPane;
import cz.zelenikr.remotetouch.dialog.LoginDialog;
import cz.zelenikr.remotetouch.dialog.RegisterDialog;
import cz.zelenikr.remotetouch.manager.SecurityManager;
import cz.zelenikr.remotetouch.manager.SettingsManager;
import impl.org.controlsfx.skin.DecorationPane;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.*;
import javafx.util.Pair;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * JavaFX application class. This is the entry point of the program.
 *
 * @author Roman Zelenik
 */
public class MainFX extends Application {

    private static final double MIN_WIDTH = 500, MIN_HEIGHT = 500;

    private static final ResourceBundle STRINGS = Resources.loadStrings(SettingsManager.getLocale());

    private final SecurityManager securityManager = SecurityManager.getInstance();

    private SettingsManager settingsManager;
    private Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        boolean startMain = true;

        stage = primaryStage;
        stage.setScene(new Scene(new Group(), 1, 1));
        stage.getScene().getStylesheets().addAll(Resources.getStyleSheets());

        UserInfo user = showLogin();
        if (user == null || user.getPassword() == null) {
            close();
            return;
        } else {
            settingsManager = SettingsManager.unlockInstance(user.getPassword());
        }

        if (shouldShowWizard()) {
            startMain = showWizard();
        }

        if (startMain) {
            showMainWindow();
        } else {
            close();
        }
    }

    private void close() {
        stage.close();
    }

    private SettingsManager getSettings() {
        return settingsManager;
    }

    private boolean shouldShowWizard() {
        return !getSettings().containsDeviceName() || !getSettings().containsPairKey();
    }

    private void showMainWindow() throws IOException {
        Pair<Node, Controller> pair = Resources.loadView("view/app.fxml", STRINGS);
        AppController controller = (AppController) pair.getValue();

        stage.setOnHidden(event -> {
            controller.onClose();
            Platform.exit();
        });
        stage.setTitle(STRINGS.getString(Resources.Strings.APPLICATION_TITLE));
        stage.setScene(new Scene((Parent) pair.getKey(), MIN_WIDTH, MIN_HEIGHT));
        stage.setMinHeight(MIN_HEIGHT);
        stage.setMinWidth(MIN_WIDTH);
        stage.show();
    }

    /**
     * Shows to the user an app initialization wizard.
     *
     * @return true if wizard was successfully completed
     */
    private boolean showWizard() throws IOException {
        AtomicBoolean success = new AtomicBoolean(false);

        Window owner = stage;

        // createUrlValidator wizard
        Wizard wizard = new Wizard(owner, STRINGS.getString(Resources.Strings.APPLICATION_TITLE));

        // createUrlValidator pages
        Pair<Node, Controller> pairDeviceView = Resources.loadView("view/settings/pair_device.fxml", STRINGS);
        PairDeviceController pairDeviceController = (PairDeviceController) pairDeviceView.getValue();

        DecorationPane content = new DecorationPane();
        content.getChildren().add(pairDeviceView.getKey());

        WizardPane page1 = new LocalizedWizardPane() {
            @Override
            public void onEnteringPage(Wizard wizard) {
                super.onEnteringPage(wizard);
                List<ButtonType> buttons = getButtonTypes().filtered(buttonType -> buttonType.getButtonData().compareTo(ButtonBar.ButtonData.BACK_PREVIOUS) == 0);
                if (!buttons.isEmpty()) {
                    Button backButton = (Button) lookupButton(buttons.get(0));
                    backButton.disabledProperty().addListener((observable, oldValue, newValue) -> backButton.setVisible(!newValue));
                }
                wizard.invalidProperty().unbind();
                wizard.invalidProperty().bind(pairDeviceController.getValidationSupport().invalidProperty());
            }
        };
        page1.setHeaderText(STRINGS.getString(Resources.Strings.WIZARD_PAIR_DEVICE_HEADER));
        page1.setContent(content);

        wizard.setFlow(new Wizard.LinearFlow(page1));

//        System.out.println("page1: " + page1);

        // show wizard and wait for response
        wizard.showAndWait().ifPresent(result -> {
            if (result == ButtonType.FINISH) {
                //System.out.println("Wizard finished, settings: " + wizard.getSettings());
                success.set(true);
            }
        });

        return success.get();
    }

    /**
     * Shows login dialog to unlock application.
     */
    private UserInfo showLogin() {
        UserInfo user = null;

        if (securityManager.existOwner()) {
            LoginDialog loginDialog = new LoginDialog(STRINGS.getString(Resources.Strings.APPLICATION_TITLE));
            loginDialog.getDialogPane().getStylesheets().addAll(Resources.getStyleSheets());
            loginDialog.setOnAuthenticateCallback(userInfo -> userInfo != null && securityManager.authenticateOwner(userInfo));

            boolean showAgain;
            do {
                Optional<ButtonType> result = loginDialog.showAndWait();
                if (result.isPresent() && result.get() == LoginDialog.BUTTON_LOGIN) {
                    if (loginDialog.isAuthenticated()) {
                        user = loginDialog.getUser();
                        showAgain = false;
                    } else {
                        Alert errorDialog = new Alert(Alert.AlertType.ERROR, STRINGS.getString(Resources.Strings.LOGIN_ERROR_CONTENT), ButtonType.OK);
                        errorDialog.setTitle(STRINGS.getString(Resources.Strings.APPLICATION_TITLE));
                        errorDialog.setHeaderText(STRINGS.getString(Resources.Strings.LOGIN_ERROR_HEADER));
                        errorDialog.showAndWait();
                        showAgain = true;
                    }

                    continue;
                } else if (result.isPresent() && result.get() == LoginDialog.BUTTON_RESET) {
                    securityManager.resetOwner();
                    user = showRegister();
                }

                showAgain = false;

            } while (showAgain);

        } else {
            user = showRegister();
        }

        return user;
    }

    private UserInfo showRegister() {
        UserInfo user = null;
        RegisterDialog registerDialog = new RegisterDialog(STRINGS.getString(Resources.Strings.APPLICATION_TITLE));
        registerDialog.getDialogPane().getStylesheets().addAll(Resources.getStyleSheets());

        Optional<ButtonType> result = registerDialog.showAndWait();
        if (result.isPresent() && result.get() == RegisterDialog.BUTTON_CREATE) {
            user = registerDialog.getUser();
            securityManager.createOwner(user);
        }
        return user;
    }

    public static void main(String[] args) {
        Locale.setDefault(SettingsManager.getLocale());
        launch(args);
    }
}
