package cz.zelenikr.remotetouch.sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class PairDeviceController {
    @FXML
    private TextField deviceName;
    @FXML
    private TextField pairKey;
    @FXML
    private Text actiontarget;

    @FXML
    protected void onPairBtClick(ActionEvent event) {
        actiontarget.setText("Pair device button pressed");
    }
}
