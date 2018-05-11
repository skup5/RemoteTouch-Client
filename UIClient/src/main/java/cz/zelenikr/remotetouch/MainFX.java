package cz.zelenikr.remotetouch;


import cz.zelenikr.remotetouch.controller.AppController;
import cz.zelenikr.remotetouch.controller.Controller;
import cz.zelenikr.remotetouch.controller.settings.PairDeviceController;
import cz.zelenikr.remotetouch.dialog.LocalizedWizardPane;
import cz.zelenikr.remotetouch.dialog.LoginDialog;
import cz.zelenikr.remotetouch.manager.SettingsManager;
import impl.org.controlsfx.skin.DecorationPane;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
 * JavaFX application class. This is the entry point of program.
 *
 * @author Roman Zelenik
 */
public class MainFX extends Application {

    // to debugging
    private static final boolean START_CLI = false;
    ////
    private static final double MIN_WIDTH = 500, MIN_HEIGHT = 500;

    private static final ResourceBundle STRINGS = Resources.loadStrings(SettingsManager.getLocale());
    private SettingsManager settingsManager;
    private Stage stage;

    private static String[] ARGS;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        stage.setScene(new Scene(new Group(), 1, 1));
        stage.getScene().getStylesheets().addAll(Resources.getStyleSheets());
//        stage.show();

        boolean startMain = true;

        String password = showLogin();
        if (password == null || password.isEmpty()) {
            close();
            return;
        } else {
            settingsManager = SettingsManager.unlockInstance(password);
        }

        if (shouldShowWizard()) {
            startMain = showWizard();
        }

        if (startMain) {
            showMainWindow();
            if (START_CLI) startConsole();
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
        Pair<Parent, Controller> pair = Resources.loadView("view/app.fxml", STRINGS);
        AppController controller = (AppController) pair.getValue();

        stage.setOnHidden(event -> {
            controller.onClose();
            Platform.exit();
        });
        stage.setTitle(STRINGS.getString(Resources.Strings.APPLICATION_TITLE));
        stage.setScene(new Scene(pair.getKey(), MIN_WIDTH, MIN_HEIGHT));
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

        // create wizard
        Wizard wizard = new Wizard(owner, STRINGS.getString(Resources.Strings.APPLICATION_TITLE));

        // create pages
        Pair<Parent, Controller> pairDeviceView = Resources.loadView("view/settings/pair_device.fxml", STRINGS);
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
    private String showLogin() {
       // if (0 < 1) return "heslo";
        LoginDialog loginDialog = new LoginDialog(STRINGS.getString(Resources.Strings.APPLICATION_TITLE));
        loginDialog.getDialogPane().getStylesheets().addAll(Resources.getStyleSheets());
        Optional<ButtonType> result = loginDialog.showAndWait();
        if (result.isPresent() && result.get() == LoginDialog.BUTTON_LOGIN) {
            return loginDialog.getPassword();
        }
        return null;
    }

    private void startConsole() {
        Thread t = new Thread(() -> {
            try {
                Main.main(ARGS);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public static void main(String[] args) {
//        System.out.println("Clearing SettingsManager");
//        SETTINGS.setDeviceName(null);
//        SETTINGS.setPairKey(null);

        Locale.setDefault(SettingsManager.getLocale());
        ARGS = args;
        launch(args);
    }
}
