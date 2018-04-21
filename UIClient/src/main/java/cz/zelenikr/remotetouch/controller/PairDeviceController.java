package cz.zelenikr.remotetouch.controller;

import cz.zelenikr.remotetouch.MainFX;
import cz.zelenikr.remotetouch.Resources;
import cz.zelenikr.remotetouch.Settings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PairDeviceController extends GridPane implements Initializable, Validateable {

    private static final Settings SETTINGS = Settings.getInstance();
    private final ValidationSupport validationSupport = new ValidationSupport();

    private GridPane pane;

    @FXML
    private TextField deviceName;
    @FXML
    private TextField pairKey;
    @FXML
    private Text actiontarget;

    private Validator<String> deviceNameValidator, pairKeyValidator;

    public PairDeviceController() {
        final FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("view/pair_device.fxml"), MainFX.getStrings());
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
//            initialize(Resources.getStrings(SETTINGS.getLocale()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // initPane(resources);
        initControls(resources);
        initValidators(resources);
        initValidation(resources);
    }

    @Override
    public ValidationSupport getValidationSupport() {
        return validationSupport;
    }

    @Override
    public boolean isValid() {
        return !validationSupport.isInvalid();
    }

//    public Node getContent() {
//        return pane;
//    }

    private void initControls(ResourceBundle resources) {
//        int row = 0;
//
//        // Device name row
//        Label label = new Label(resources.getString(Resources.Strings.SETTINGS_DEVICE_NAME));
//        label.setWrapText(true);

//        deviceName = new TextField(SETTINGS.getDeviceName());
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

//        pane.addRow(row, label, deviceName);
//        row++;
//
//        // Pair key row
//        label = new Label(resources.getString(Resources.Strings.SETTINGS_DEVICE_PAIR_KEY));
//        label.setWrapText(true);

//        pairKey = new TextField(SETTINGS.getPairKey());
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

//        pane.addRow(row, label, pairKey);
//        row++;
    }

    private void initPane(ResourceBundle resources) {
        pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(10);
        pane.setVgap(10);
        pane.setPadding(new Insets(25, 25, 10, 25));
//        pane.getStylesheets().add( "css/style.css");
//        pane.getStyleClass().add("bg-primary");
    }

    private void initValidators(ResourceBundle resources) {
        deviceNameValidator = Validator.createEmptyValidator(resources.getString(Resources.Strings.VALIDATION_DEVICE_NAME));
        pairKeyValidator = Validator.createEmptyValidator(resources.getString(Resources.Strings.VALIDATION_DEVICE_PAIR_KEY));
    }

    private void initValidation(ResourceBundle resources) {
        validationSupport.initInitialDecoration();

        validationSupport.registerValidator(deviceName, deviceNameValidator);
        validationSupport.registerValidator(pairKey, pairKeyValidator);

    }
}
