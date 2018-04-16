package cz.zelenikr.remotetouch.controller;

import cz.zelenikr.remotetouch.MainFX;
import cz.zelenikr.remotetouch.Resources;
import cz.zelenikr.remotetouch.Settings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PairDeviceController  implements Initializable, Validateable {

    private static final Settings SETTINGS = Settings.getInstance();
    private final ValidationSupport validationSupport = new ValidationSupport();

    @FXML
    private TextField deviceName;
    @FXML
    private TextField pairKey;
    @FXML
    private Text actiontarget;

    private Validator<String> deviceNameValidator, pairKeyValidator;


    @FXML
    protected void onPairBtClick(ActionEvent event) {
        actiontarget.setText("Pair device button pressed");
    }

    public PairDeviceController(){
//        final FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("view/pair_device.fxml"), MainFX.getStrings());
//        loader.setRoot(this);
//        loader.setController(this);
//        try {
//            loader.load();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initValidators(resources);
        initValidation(resources);
        initControls(resources);
    }

    private void initValidators(ResourceBundle resources) {
        deviceNameValidator = Validator.createEmptyValidator(resources.getString(Resources.Strings.VALIDATION_DEVICE_NAME));
        pairKeyValidator = Validator.createEmptyValidator(resources.getString(Resources.Strings.VALIDATION_DEVICE_PAIR_KEY));
    }

    @Override
    public ValidationSupport getValidationSupport() {
        return validationSupport;
    }

    @Override
    public boolean isValid() {
        return !validationSupport.isInvalid();
    }

    private void initControls(ResourceBundle resources) {
        deviceName.setText(SETTINGS.getDeviceName());
        deviceName.focusedProperty().addListener((observable, oldValue, newValue) -> {
            // when focus lost
            if (!newValue) {
                // text is valid
                if (deviceNameValidator.apply(deviceName, deviceName.getText()).getErrors().isEmpty()) {
                    // store device name
                    SETTINGS.setDeviceName(deviceName.getText());
                }
            }
        });

        pairKey.setText(SETTINGS.getPairKey());
        pairKey.focusedProperty().addListener((observable, oldValue, newValue) -> {
            // when focus lost
            if (!newValue) {
                // text is valid
                if (pairKeyValidator.apply(pairKey, pairKey.getText()).getErrors().isEmpty()) {
                    // store pair key
                    SETTINGS.setPairKey(pairKey.getText());
                }
            }
        });

    }

    private void initValidation(ResourceBundle resources) {
        validationSupport.initInitialDecoration();

        validationSupport.registerValidator(deviceName, deviceNameValidator);
        validationSupport.registerValidator(pairKey, pairKeyValidator);

    }
}
