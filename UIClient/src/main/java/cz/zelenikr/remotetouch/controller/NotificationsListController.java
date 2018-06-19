package cz.zelenikr.remotetouch.controller;

import cz.zelenikr.remotetouch.data.comparator.NotificationByDatetimeComparator;
import cz.zelenikr.remotetouch.data.dao.NotificationEventContentDAO;
import cz.zelenikr.remotetouch.data.dao.NotificationEventContentDAOMobile;
import cz.zelenikr.remotetouch.data.dto.event.NotificationEventContent;
import cz.zelenikr.remotetouch.view.listCell.NotificationListCell;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * The notifications {@link ListView} controller.
 *
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
        list.setItems(new SortedList<>(data, new NotificationByDatetimeComparator()));
        list.setCellFactory(listView -> {
            NotificationListCell cell = new NotificationListCell(listView);
            cell.setCloseEventHandler(this::onCloseCell);
            return cell;
        });
    }

    private void onCloseCell(MouseEvent mouseEvent) {
        Parent source = (Parent) mouseEvent.getSource();
        Object userData = source.getUserData();
//        System.out.println("Close cell " + userData);
        if (userData != null)
            removeItem((NotificationEventContent) userData);
    }

    private void onNewNotification(NotificationEventContent content) {
        if (!data.contains(content))
            Platform.runLater(() -> data.add(content));
    }

    private void removeItem(NotificationEventContent item) {
        data.remove(item);
    }

}
