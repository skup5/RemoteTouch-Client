package cz.zelenikr.remotetouch.controller;

import cz.zelenikr.remotetouch.data.comparator.CallByDatetimeComparator;
import cz.zelenikr.remotetouch.data.dao.CallEventContentDAO;
import cz.zelenikr.remotetouch.data.dao.CallEventContentDAOMobile;
import cz.zelenikr.remotetouch.data.event.CallEventContent;
import cz.zelenikr.remotetouch.view.listCell.CallListCell;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * @author Roman Zelenik
 */
public class CallsListController implements Controller, Initializable {

    @FXML
    private ListView<CallEventContent> list;

    private final ObservableList<CallEventContent> data = FXCollections.observableArrayList();

    private final CallEventContentDAO callDAO = new CallEventContentDAOMobile();

    public CallsListController() {
        callDAO.setOnNewItemCallback(this::onNewCall);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadData();
        list.setItems(new SortedList<>(data, new CallByDatetimeComparator()));
        list.setCellFactory(listView -> new CallListCell());
    }

    private void loadData() {
        callDAO.loadAllAsync(calls -> Platform.runLater(() -> data.addAll(calls)));
    }

    private void onNewCall(CallEventContent content) {
        Platform.runLater(() -> data.add(content));
    }
}
