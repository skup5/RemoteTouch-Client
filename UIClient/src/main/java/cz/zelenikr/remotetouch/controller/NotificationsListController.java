package cz.zelenikr.remotetouch.controller;

import cz.zelenikr.remotetouch.data.dao.NotificationEventContentDAO;
import cz.zelenikr.remotetouch.data.dao.NotificationEventContentDAOMobile;
import cz.zelenikr.remotetouch.data.event.NotificationEventContent;
import cz.zelenikr.remotetouch.view.listCell.NotificationListCell;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Roman Zelenik
 */
public class NotificationsListController implements Controller, Initializable {

    @FXML
    private ListView<NotificationEventContent> list;

    private final ObservableList<NotificationEventContent> data = FXCollections.observableArrayList();

    private final NotificationEventContentDAO notificationDAO = new NotificationEventContentDAOMobile();

    public NotificationsListController() {
        notificationDAO.setOnNewItemCallback(this::onNewNotification);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadData();
        list.setItems(data);
        list.setCellFactory(listView -> {
            return new NotificationListCell();
        });
    }

    private void loadData() {
        notificationDAO.loadAllAsync(notifications -> Platform.runLater(() -> data.addAll(notifications)));
    }

    private void onNewNotification(NotificationEventContent content) {
        Platform.runLater(() -> data.add(content));
    }

}
