package cz.zelenikr.remotetouch.controller;

import cz.zelenikr.remotetouch.MainFX;
import cz.zelenikr.remotetouch.data.dto.event.CallEventContent;
import cz.zelenikr.remotetouch.data.dto.event.NotificationEventContent;
import cz.zelenikr.remotetouch.data.dto.event.SmsEventContent;
import cz.zelenikr.remotetouch.data.mapper.ConnectionStatusToLocaleStringMapper;
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
        connectionManager.registerConnectionStateChangedListener(this::onConnectionStateChangedAsync);
        connectionManager.registerCallReceivedListener(this::onNewCallsAsync);
        connectionManager.registerNotificationReceivedListener(this::onNewNotificationsAsync);
        connectionManager.registerSmsReceivedListener(this::onNewSmsAsync);
        connectionManager.connect();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        onConnectionStateChanged(ConnectionStatus.DISCONNECTED);
    }

    public void onClose() {
        LOGGER.info("close called");
        connectionManager.disconnect();
    }

    private void onConnectionStateChanged(ConnectionStatus status) {
        connectionStatus.setText(ConnectionStatusToLocaleStringMapper.toString(status));
    }

    private void onConnectionStateChangedAsync(ConnectionStatus status) {
//        LOGGER.info(status.toString());
        Platform.runLater(() -> onConnectionStateChanged(status));
    }

    private void onNewCallsAsync(CallEventContent... calls) {
        for (CallEventContent content : calls)
            Platform.runLater(() -> MainFX.notification(Pos.BOTTOM_RIGHT, "Nový hovor", content.toString()));
    }

    private void onNewNotificationsAsync(NotificationEventContent... notifications) {
        for (NotificationEventContent content : notifications)
            Platform.runLater(() -> MainFX.notification(Pos.BOTTOM_RIGHT, content.getLabel() + ": " + content.getTitle(), content.getText()));
    }

    private void onNewSmsAsync(SmsEventContent... sms) {
        for (SmsEventContent content : sms)
            Platform.runLater(() -> MainFX.notification(Pos.BOTTOM_RIGHT, "Nová sms", content.toString()));
    }
}
