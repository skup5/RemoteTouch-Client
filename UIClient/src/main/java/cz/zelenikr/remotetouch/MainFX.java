package cz.zelenikr.remotetouch;


import cz.zelenikr.remotetouch.controller.AppController;
import cz.zelenikr.remotetouch.controller.Controller;
import cz.zelenikr.remotetouch.controller.settings.PairDeviceController;
import impl.org.controlsfx.skin.DecorationPane;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.*;
import javafx.util.Duration;
import javafx.util.Pair;
import org.controlsfx.control.Notifications;
import org.controlsfx.dialog.LoginDialog;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Locale;
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

    private static final Settings SETTINGS = Settings.getInstance();
    private static final double MIN_WIDTH = 500, MIN_HEIGHT=500;
    private Stage stage;

    private static String[] ARGS;
    private static Stage dummyStage;

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
        return !SETTINGS.containsDeviceName() || !SETTINGS.containsPairKey();
    }

    private void showMainWindow() throws IOException {
        Pair<Parent, Controller> pair = loadView("view/app.fxml");
        ResourceBundle strings = getStrings();
        AppController controller = (AppController) pair.getValue();

        stage.setOnHidden(event -> {
            controller.onClose();
            if (dummyStage != null) dummyStage.close();
        });
        stage.setTitle(strings.getString(Resources.Strings.APPLICATION_TITLE));
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
        ResourceBundle strings = getStrings();

        Window owner = stage;

        // create wizard
        Wizard wizard = new Wizard(owner, strings.getString(Resources.Strings.APPLICATION_TITLE));

        // create pages
        Pair<Parent, Controller> pairDeviceView = loadView("view/settings/pair_device.fxml");
        PairDeviceController pairDeviceController = (PairDeviceController) pairDeviceView.getValue();

        DecorationPane content = new DecorationPane();
        content.getChildren().add(pairDeviceView.getKey());

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

        notificationBuilder.owner(getNotificationOwner());
//            notificationBuilder.hideCloseButton();
//         notificationBuilder.darkStyle();

        notificationBuilder.showInformation();
    }

    /**
     * @param name view resource name
     * @return view for the given resource name and his {@link Controller}
     */
    private Pair<Parent, Controller> loadView(@NotNull String name) throws IOException {
        FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource(name), getStrings());
        Parent view = loader.load();
        Controller controller = loader.getController();
        return new Pair<>(view, controller);
    }

    private static Stage createDummyStage() {
        Stage dummyPopup = new Stage();
        dummyPopup.initModality(Modality.NONE);
        // set as utility so no iconification occurs
        dummyPopup.initStyle(StageStyle.UTILITY);
        // set opacity so the window cannot be seen
        dummyPopup.setOpacity(0d);
        // not necessary, but this will move the dummy stage off the screen
        final Screen screen = Screen.getPrimary();
        final Rectangle2D bounds = screen.getVisualBounds();
        dummyPopup.setX(bounds.getMaxX()-25);
        dummyPopup.setY(bounds.getMaxY()-40);
        // create/add a transparent scene
        final Group root = new Group();
        dummyPopup.setScene(new Scene(root, 1d, 1d, Color.TRANSPARENT));
        // show the dummy stage
        dummyPopup.show();
        return dummyPopup;
    }

    private static Stage getNotificationOwner() {
        if (dummyStage == null) {
            dummyStage = createDummyStage();
        }
        return dummyStage;
    }

    public static ResourceBundle getStrings() {
        return Resources.getStrings(SETTINGS.getLocale());
    }

    public static void main(String[] args) {
//        System.out.println("Clearing Settings");
//        SETTINGS.setDeviceName(null);
//        SETTINGS.setPairKey(null);

        Locale.setDefault(SETTINGS.getLocale());
        ARGS = args;
        launch(args);
    }
}
