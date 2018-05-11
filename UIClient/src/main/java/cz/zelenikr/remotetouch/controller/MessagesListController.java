package cz.zelenikr.remotetouch.controller;

import cz.zelenikr.remotetouch.data.comparator.SmsByDatetimeComparator;
import cz.zelenikr.remotetouch.data.dao.SmsEventContentDAO;
import cz.zelenikr.remotetouch.data.dao.SmsEventContentDAOMobile;
import cz.zelenikr.remotetouch.data.dto.event.SmsEventContent;
import cz.zelenikr.remotetouch.view.listCell.MessageListCell;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Roman Zelenik
 */
public class MessagesListController implements Controller, Initializable {

    @FXML
    private ListView<SmsEventContent> list;

    private final ObservableList<SmsEventContent> data = FXCollections.observableArrayList();

    private final SmsEventContentDAO smsDAO = new SmsEventContentDAOMobile();

    public MessagesListController() {
        smsDAO.setOnNewItemCallback(this::onNewSms);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadData();
        list.setItems(new SortedList<>(data, new SmsByDatetimeComparator()));
        list.setCellFactory(listView -> {
            MessageListCell cell = new MessageListCell(listView);
            cell.setCloseEventHandler(this::onCloseCell);
            return cell;
        });
    }

    private void loadData() {
        smsDAO.loadAllAsync(messages -> Platform.runLater(() -> data.addAll(messages)));
    }

    private void onCloseCell(MouseEvent mouseEvent) {
        Parent source = (Parent) mouseEvent.getSource();
        Object userData = source.getUserData();
//        System.out.println("Close cell " + userData);
        if (userData != null)
            removeItem((SmsEventContent) userData);
    }

    private void onNewSms(SmsEventContent content) {
        Platform.runLater(() -> data.add(content));
    }

    private void removeItem(SmsEventContent item) {
        data.remove(item);
    }
}
