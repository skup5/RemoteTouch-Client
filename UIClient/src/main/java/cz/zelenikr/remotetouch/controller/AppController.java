package cz.zelenikr.remotetouch.controller;

import cz.zelenikr.remotetouch.Resources;
import cz.zelenikr.remotetouch.data.dto.event.CallEventContent;
import cz.zelenikr.remotetouch.data.dto.event.NotificationEventContent;
import cz.zelenikr.remotetouch.data.dto.event.SmsEventContent;
import cz.zelenikr.remotetouch.data.formatter.EventDateTimeFormat;
import cz.zelenikr.remotetouch.data.mapper.CallTypeToLocalStringMapper;
import cz.zelenikr.remotetouch.data.mapper.ConnectionStatusToLocaleStringMapper;
import cz.zelenikr.remotetouch.dialog.AboutDialog;
import cz.zelenikr.remotetouch.manager.ConnectionManager;
import cz.zelenikr.remotetouch.manager.NotificationManager;
import cz.zelenikr.remotetouch.network.ConnectionStatus;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * The app main window controller.
 *
 * @author Roman Zelenik
 */
public class AppController implements Controller, Initializable {

    private static final Logger LOGGER = Logger.getLogger(AppController.class.getSimpleName());

    private final ConnectionManager connectionManager = ConnectionManager.getInstance();
    private final NotificationManager notificationManager = NotificationManager.getInstance();

    private final EventHandler<ActionEvent> focusNotificationsTab, focusCallsTab, focusMessagesTab;
    private final EventDateTimeFormat dateFormat = new EventDateTimeFormat();

    private ResourceBundle resources;
    private Stage stage;

    @FXML
    private Tab settingsTab;

    private String callNtfTitle, notificationNtfTitle, smsNtfTitle;

    @FXML
    TabPane tabPane;
    @FXML
    private Tab callsTab, notificationsTab, messagesTab;

    @FXML
    private Label connectionStatus, connectionStatusIcon;

    /////////////////////////
    // App menu items actions
    /////////////////////////
    @FXML
    private void onPrefsMenuItemClick(ActionEvent event) {
        addTab(settingsTab);
    }

    @FXML
    private void onQuitMenuItemClick(ActionEvent event) {
        if (getStage() != null) getStage().close();
    }

    ////////////////////////////////
    // Connection menu items actions
    ////////////////////////////////
    @FXML
    private void onConnectMenuItemClick(ActionEvent event) {
        connectionManager.connect();
    }

    @FXML
    private void onDisconnectMenuItemClick(ActionEvent event) {
        connectionManager.disconnect();
    }

    @FXML
    private void onReconnectMenuItemClick(ActionEvent event) {
        connectionManager.reconnect();
    }

    //////////////////////////
    // Help menu items actions
    //////////////////////////
    @FXML
    private void onAboutMenuItemClick(ActionEvent event) {
        new AboutDialog(getString(Resources.Strings.APPLICATION_TITLE)).showAndWait();
    }

    public AppController() {
        focusCallsTab = event -> {
            tabPane.getSelectionModel().select(callsTab);
            toFront();
        };
        focusNotificationsTab = event -> {
            tabPane.getSelectionModel().select(notificationsTab);
            toFront();
        };
        focusMessagesTab = event -> {
            tabPane.getSelectionModel().select(messagesTab);
            toFront();
        };
        connectionManager.registerConnectionStateChangedListener(this::onConnectionStateChangedAsync);
        connectionManager.registerCallReceivedListener(this::onNewCallsAsync);
        connectionManager.registerNotificationReceivedListener(this::onNewNotificationsAsync);
        connectionManager.registerSmsReceivedListener(this::onNewSmsAsync);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;

        callNtfTitle = getString(Resources.Strings.NOTIFICATION_TITLE_CALL);
        notificationNtfTitle = getString(Resources.Strings.NOTIFICATION_TITLE_NOTIFICATION);
        smsNtfTitle = getString(Resources.Strings.NOTIFICATION_TITLE_SMS);

        onConnectionStateChanged(ConnectionStatus.DISCONNECTED);
        connectionManager.connect();
    }

    public void onClose() {
        LOGGER.info("close called");
        connectionManager.disconnect();
    }

    private void addTab(Tab tab) {
        if (!tabPane.getTabs().contains(tab)) {
            tabPane.getTabs().add(tab);
        }
    }

    private Stage getStage() {
        try {
            if (stage == null)
                stage = (Stage) notificationsTab.getTabPane().getScene().getWindow();
        } catch (NullPointerException e) {
        }
        return stage;
    }

    /**
     * Gets a string for the given key from this resource bundle or one of its parents.
     *
     * @param key – the key for the desired string
     * @return the string for the given key
     */
    private String getString(String key) {
        return resources.getString(key);
    }

    private void onConnectionStateChanged(ConnectionStatus status) {
        connectionStatus.setText(ConnectionStatusToLocaleStringMapper.toString(status));
        connectionStatusIcon.setGraphic(Resources.Icons.getIconByConnectionStatus(status));
    }

    private void onConnectionStateChangedAsync(ConnectionStatus status) {
//        LOGGER.info(status.toString());
        Platform.runLater(() -> onConnectionStateChanged(status));
    }

    private void onNewCallsAsync(CallEventContent... calls) {
        Platform.runLater(() -> {
            for (CallEventContent content : calls) {
                //                final String title = content.getName() == null || content.getName().isEmpty() ? content.getNumber() : content.getName();
                notificationManager.notify(
                        Resources.Icons.getIconByCallType(content.getType()),
                        callNtfTitle + " · " + dateFormat.format(content.getWhen()),
                        CallTypeToLocalStringMapper.toString(content.getType()),
                        focusCallsTab);
            }
        });
    }

    private void onNewNotificationsAsync(NotificationEventContent... notifications) {
        Platform.runLater(() -> {
            for (NotificationEventContent content : notifications) {
                notificationManager.notify(
                        Resources.Icons.getIconByApp(content.getApp()),
                        notificationNtfTitle + " · " + dateFormat.format(content.getWhen()),
                        content.getLabel() + "\n" + content.getTitle(),
                        focusNotificationsTab);
            }
        });
    }

    private void onNewSmsAsync(SmsEventContent... sms) {
        Platform.runLater(() -> {
            for (SmsEventContent content : sms) {
//                final String title = content.getName() == null || content.getName().isEmpty() ? content.getNumber() : content.getName();
                notificationManager.notify(
                        Resources.Icons.getSmsIcon(),
                        smsNtfTitle + " · " + dateFormat.format(content.getWhen()),
                        "Sms",
                        focusMessagesTab);
            }
        });
    }

    /**
     * Moves app window to the front on the screen.
     */
    private void toFront() {
        Stage stage = getStage();
        if (stage != null) {
            stage.setIconified(false);
            stage.toFront();
        }
    }
}
