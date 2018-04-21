package cz.zelenikr.remotetouch.controller;

import com.sun.javafx.PlatformUtil;
import com.sun.javafx.util.Logging;
import cz.zelenikr.remotetouch.manager.ConnectionManager;
import cz.zelenikr.remotetouch.network.ConnectionStatus;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;


import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * @author Roman Zelenik
 */
public class AppController implements Controller, Initializable {

    private static final Logger LOGGER = Logger.getLogger(AppController.class.getSimpleName());

    private final ConnectionManager connectionManager = ConnectionManager.getInstance();

    @FXML
    private Label connectionStatus;

    public AppController() {
        connectionManager.registerConnectionStateChangedListener(this::onConnectionStateChanged);
        connectionManager.connect();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void onClose() {
        LOGGER.info("close called");
        connectionManager.disconnect();
    }

    private Void onConnectionStateChanged(ConnectionStatus status) {
//        LOGGER.info(status.toString());
        Platform.runLater(() -> connectionStatus.setText(status.toString()));
        return null;
    }

}
