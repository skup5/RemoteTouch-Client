package cz.zelenikr.remotetouch.controller.settings;

import cz.zelenikr.remotetouch.Resources;
import cz.zelenikr.remotetouch.controller.Controller;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import javafx.util.Pair;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Roman Zelenik
 */
public class SettingsTabController implements Controller, Initializable {

    @FXML
    private VBox connectionPaneContent, devicePaneContent;

    @FXML
    private TextFlow connectionHeader, deviceHeader;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Pair<Node, Controller> view = Resources.loadView("view/settings/connection.fxml", resources);
            connectionPaneContent.getChildren().add(view.getKey());
            ConnectionController cController = (ConnectionController) view.getValue();
            connectionHeader.visibleProperty().bind(cController.getChangedProperty());

            view = Resources.loadView("view/settings/pair_device.fxml", resources);
            devicePaneContent.getChildren().add(view.getKey());
            PairDeviceController pdController = (PairDeviceController) view.getValue();
            deviceHeader.visibleProperty().bind(pdController.getChangedProperty());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
