package cz.zelenikr.remotetouch;

import com.sun.istack.internal.NotNull;
import cz.zelenikr.remotetouch.controller.PairDeviceController;
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
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainFX extends Application {

    private static final Settings SETTINGS = Settings.getInstance();
    private Stage stage;

    private static String[] ARGS;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
//        stage.setScene(new Scene(new Group(), 1, 1));
//        stage.show();

        boolean startMain = true;

        if (shouldShowWizard()) {
            //startMain = showWizard();
        }

        if (startMain) {
            showMainWindow();
            Thread t = new Thread(()-> {
                try {
                    Main.main(ARGS);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            });
            t.setDaemon(true);
            t.start();
        } else {
            stage.close();
        }
    }

    private boolean shouldShowWizard() {
        return !SETTINGS.containsDeviceName() || !SETTINGS.containsPairKey();
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

        Stage owner = new Stage();
        Scene scene = new Scene(new Pane());
        owner.setScene(scene);
        owner.initOwner(stage);
        owner.initModality(Modality.WINDOW_MODAL);
        owner = null;

        // create wizard
        Wizard wizard = new Wizard(owner, strings.getString(Resources.Strings.APPLICATION_TITLE));

        // create pages
        WizardPane page1 = new WizardPane();

        page1.setHeaderText("Please Enter Your Details");
//        page1.setContent(new Group(new PairDeviceController()));
        page1.setContent(new Group(loadView("view/pair_device.fxml")));
//        page1.setContent(new Group(new GridPane()));

        wizard.setFlow(new Wizard.LinearFlow(page1));

        System.out.println("page1: " + page1);

        // show wizard and wait for response
        wizard.showAndWait().ifPresent(result -> {
            if (result == ButtonType.FINISH) {
                System.out.println("Wizard finished, settings: " + wizard.getSettings());
                success.set(true);
            }
        });

        return success.get();
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

//    private void replaceContent(Pane pane){
//        // replace the content
//        StackPane content = (StackPane) ((VBox) stage.getScene().getRoot()).getChildren().get(1);
//        content.getChildren().clear();
//        content.getChildren().add(pane);
//    }

    public static ResourceBundle getStrings() {
        return Resources.getStrings(SETTINGS.getLocale());
    }

    public static void main(String[] args) {
        ARGS = args;
        launch(args);
    }
}
