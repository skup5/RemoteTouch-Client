package cz.zelenikr.remotetouch;

import com.sun.istack.internal.NotNull;
import cz.zelenikr.remotetouch.controller.PairDeviceController;
import impl.org.controlsfx.i18n.Localization;
import impl.org.controlsfx.skin.DecorationPane;
import javafx.application.Application;
import javafx.collections.ObservableMap;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.Pair;
import org.controlsfx.control.Notifications;
import org.controlsfx.dialog.LoginDialog;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;
import sun.font.Decoration;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainFX extends Application {

    // to debugging
    private static final boolean START_CLI = false;

    private static final Settings SETTINGS = Settings.getInstance();
    private Stage stage;

    private static String[] ARGS;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        stage.setScene(new Scene(new Group(), 1, 1));
        stage.getScene().getStylesheets().addAll(Resources.getStyleSheets());
//        stage.show();

        boolean startMain = true;

//        showLogin();

        if (shouldShowWizard()) {
            startMain = showWizard();
        }

        if (startMain) {
            showMainWindow();
            if (START_CLI) startConsole();
        } else {
            stage.close();
        }
    }

    private boolean shouldShowWizard() {
        return true || !SETTINGS.containsDeviceName() || !SETTINGS.containsPairKey();
    }

    private void showMainWindow() throws IOException {
        Parent root = loadView("view/app.fxml");
        ResourceBundle strings = getStrings();

        stage.setTitle(strings.getString(Resources.Strings.APPLICATION_TITLE));
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Shows to the user an app initialization wizard.
     *
     * @return true if wizard was successfully completed
     * @throws IOException
     */
    private boolean showWizard() throws IOException {
        AtomicBoolean success = new AtomicBoolean(false);
        ResourceBundle strings = getStrings();

        Window owner = stage;

        // create wizard
        Wizard wizard = new Wizard(owner, strings.getString(Resources.Strings.APPLICATION_TITLE));

        // create pages
        PairDeviceController pairDeviceController = new PairDeviceController();

        DecorationPane content = new DecorationPane();
        content.getChildren().add(pairDeviceController);

        WizardPane page1 = new WizardPane() {
            @Override
            public void onEnteringPage(Wizard wizard) {
                wizard.invalidProperty().unbind();
                wizard.invalidProperty().bind(pairDeviceController.getValidationSupport().invalidProperty());
            }
        };
        page1.setHeaderText(strings.getString(Resources.Strings.WIZARD_PAIR_DEVICE_HEADER));
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
    private void showLogin() {
        LoginDialog loginDialog = new LoginDialog(new Pair<>("", ""), param -> {
            return null;
        });
        loginDialog.getDialogPane().getStylesheets().addAll(Resources.getStyleSheets());
        loginDialog.showAndWait();

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

    public static void notification(Pos pos, String title, String text) {
        Notifications notificationBuilder = Notifications.create()
                .title(title)
                .text(text)
                .hideAfter(Duration.INDEFINITE)
                .position(pos)
                .onAction(e -> System.out.println("Notification clicked on!"));

//            notificationBuilder.owner(stage);
//            notificationBuilder.hideCloseButton();
//         notificationBuilder.darkStyle();

        notificationBuilder.showInformation();
    }

    /**
     * @param name view resource name
     * @return view for the given resource name
     */
    private Parent loadView(@NotNull String name) throws IOException {
        Parent root = FXMLLoader.load(ClassLoader.getSystemResource(name), getStrings());
        return root;
    }

    public static ResourceBundle getStrings() {
        return Resources.getStrings(SETTINGS.getLocale());
    }

    public static void main(String[] args) {
        System.out.println("Clearing Settings");
        SETTINGS.setDeviceName("");
        SETTINGS.setPairKey("");

        Locale.setDefault(SETTINGS.getLocale());
        ARGS = args;
        launch(args);
    }
}
