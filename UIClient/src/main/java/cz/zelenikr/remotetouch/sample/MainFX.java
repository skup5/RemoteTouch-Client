package cz.zelenikr.remotetouch.sample;

import com.sun.istack.internal.NotNull;
import cz.zelenikr.remotetouch.Resources;
import cz.zelenikr.remotetouch.Settings;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ResourceBundle;

public class MainFX extends Application {

    private static final Settings SETTINGS = Settings.getInstance();
    private Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.stage = primaryStage;

        Parent root = loadView("view/app.fxml");
        ResourceBundle strings = getStrings();

        primaryStage.setTitle(strings.getString(Resources.Strings.APPLICATION_TITLE));
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
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

    private static ResourceBundle getStrings() {
        return Resources.getStrings(SETTINGS.getLocale());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
