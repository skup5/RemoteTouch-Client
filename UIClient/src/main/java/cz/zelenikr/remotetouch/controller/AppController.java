package cz.zelenikr.remotetouch.controller;

import cz.zelenikr.remotetouch.MainFX;
import cz.zelenikr.remotetouch.Resources;
import cz.zelenikr.remotetouch.data.dto.event.CallEventContent;
import cz.zelenikr.remotetouch.data.dto.event.NotificationEventContent;
import cz.zelenikr.remotetouch.data.dto.event.SmsEventContent;
import cz.zelenikr.remotetouch.data.mapper.CallTypeToLocalStringMapper;
import cz.zelenikr.remotetouch.data.mapper.ConnectionStatusToLocaleStringMapper;
import cz.zelenikr.remotetouch.manager.ConnectionManager;
import cz.zelenikr.remotetouch.manager.NotificationManager;
import cz.zelenikr.remotetouch.network.ConnectionStatus;
import de.jensd.fx.glyphs.GlyphIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import de.jensd.fx.glyphs.materialicons.MaterialIconView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * @author Roman Zelenik
 */
public class AppController implements Controller, Initializable {

    private static final Logger LOGGER = Logger.getLogger(AppController.class.getSimpleName());

    private final ConnectionManager connectionManager = ConnectionManager.getInstance();
    private final NotificationManager notificationManager = NotificationManager.getInstance();

    private final EventHandler<ActionEvent> focusNotificationsTab, focusCallsTab, focusMessagesTab;

    private Stage stage;

    @FXML
    private Tab callsTab, notificationsTab, messagesTab;

    @FXML
    private Label connectionStatus;

    public AppController() {
        focusCallsTab = event -> {
            notificationsTab.getTabPane().getSelectionModel().select(callsTab);
            toFront();
        };
        focusNotificationsTab = event -> {
            notificationsTab.getTabPane().getSelectionModel().select(notificationsTab);
            toFront();
        };
        focusMessagesTab = event -> {
            notificationsTab.getTabPane().getSelectionModel().select(messagesTab);
            toFront();
        };
        connectionManager.registerConnectionStateChangedListener(this::onConnectionStateChangedAsync);
        connectionManager.registerCallReceivedListener(this::onNewCallsAsync);
        connectionManager.registerNotificationReceivedListener(this::onNewNotificationsAsync);
        connectionManager.registerSmsReceivedListener(this::onNewSmsAsync);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        onConnectionStateChanged(ConnectionStatus.DISCONNECTED);
        connectionManager.connect();
    }

    public void onClose() {
        LOGGER.info("close called");
        connectionManager.disconnect();
    }

    private Stage getStage() {
        try {
            if (stage == null)
                stage = (Stage) notificationsTab.getTabPane().getScene().getWindow();
        } catch (NullPointerException e) {
        }
        return stage;
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
            Platform.runLater(() -> {
                final String title = content.getName() == null || content.getName().isEmpty() ? content.getNumber() : content.getName();
                notificationManager.notify(
                        Resources.Icons.getIconByCallType(content.getType()),
                        title,
                        CallTypeToLocalStringMapper.toString(content.getType()),
                        focusCallsTab);
            });
    }

    private void onNewNotificationsAsync(NotificationEventContent... notifications) {
        for (NotificationEventContent content : notifications)
            Platform.runLater(() -> notificationManager.notify(
                    Resources.Icons.getIconByApp(content.getApp()),
                    content.getLabel(),
                    content.getTitle(),
                    focusNotificationsTab));
    }

    private void onNewSmsAsync(SmsEventContent... sms) {
        for (SmsEventContent content : sms)
            Platform.runLater(() -> {
                final String title = content.getName() == null || content.getName().isEmpty() ? content.getNumber() : content.getName();
                notificationManager.notify(
                        Resources.Icons.getSmsIcon(),
                        title,
                        "",
                        focusMessagesTab);
            });
    }

    private void toFront() {
        Stage stage = getStage();
        if (stage != null) {
            stage.setIconified(false);
            stage.toFront();
        }
    }
}
