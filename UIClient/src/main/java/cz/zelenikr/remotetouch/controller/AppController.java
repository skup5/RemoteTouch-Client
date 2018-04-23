package cz.zelenikr.remotetouch.controller;

import cz.zelenikr.remotetouch.MainFX;
import cz.zelenikr.remotetouch.data.event.CallEventContent;
import cz.zelenikr.remotetouch.data.event.NotificationEventContent;
import cz.zelenikr.remotetouch.data.event.SmsEventContent;
import cz.zelenikr.remotetouch.manager.ConnectionManager;
import cz.zelenikr.remotetouch.network.ConnectionStatus;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
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
        connectionManager.registerCallReceivedListener(this::onNewCalls);
        connectionManager.registerNotificationReceivedListener(this::onNewNotifications);
        connectionManager.registerSmsReceivedListener(this::onNewSms);
        connectionManager.connect();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void onClose() {
        LOGGER.info("close called");
        connectionManager.disconnect();
    }

    private void onConnectionStateChanged(ConnectionStatus status) {
//        LOGGER.info(status.toString());
        Platform.runLater(() -> connectionStatus.setText(status.toString()));
    }

    private void onNewCalls(CallEventContent... calls) {
        for (CallEventContent content : calls)
            Platform.runLater(() -> MainFX.notification(Pos.BOTTOM_RIGHT, "Nový hovor", content.toString()));
    }

    private void onNewNotifications(NotificationEventContent... notifications) {
        for (NotificationEventContent content : notifications)
            Platform.runLater(() -> MainFX.notification(Pos.BOTTOM_RIGHT, content.getLabel(), content.getTitle()));
    }

    private void onNewSms(SmsEventContent... sms) {
        for (SmsEventContent content : sms)
            Platform.runLater(() -> MainFX.notification(Pos.BOTTOM_RIGHT, "Nová sms", content.toString()));
    }
}
