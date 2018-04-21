package cz.zelenikr.remotetouch.controller;

import cz.zelenikr.remotetouch.Resources;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Roman Zelenik
 */
public class NavigationController implements Initializable {

    private ResourceBundle resources;

    @FXML
    ListView<String> navigationList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources=resources;
        ObservableList<String> items = FXCollections.observableArrayList(loadItems());
        navigationList.setItems(items);
        navigationList.getSelectionModel().selectedItemProperty().addListener(
                (ov, old_val, new_val) -> onItemSelected(new_val));
    }

    private void onItemSelected(String item) {

    }

    private List<String> loadItems() {
        ArrayList list = new ArrayList<>();
        list.add(getResources().getString(Resources.Strings.NAVIGATION_ITEMS_CALLS));
        list.add(getResources().getString(Resources.Strings.NAVIGATION_ITEMS_MESSAGES));

        return list;
    }

    private ResourceBundle getResources(){
        return resources;
    }
}
