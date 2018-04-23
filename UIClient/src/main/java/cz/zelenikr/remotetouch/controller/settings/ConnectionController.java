package cz.zelenikr.remotetouch.controller.settings;

import cz.zelenikr.remotetouch.Resources;
import cz.zelenikr.remotetouch.Settings;
import cz.zelenikr.remotetouch.controller.Controller;
import cz.zelenikr.remotetouch.controller.Validateable;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Roman Zelenik
 */
public class ConnectionController implements Controller, Initializable, Validateable {

    private static final Settings SETTINGS = Settings.getInstance();
    private final ValidationSupport validationSupport = new ValidationSupport();

    @FXML
    private TextField address;

    private Validator<String> addressValidator;

    @Override
    public ValidationSupport getValidationSupport() {
        return validationSupport;
    }

    @Override
    public boolean isValid() {
        return !validationSupport.isInvalid();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initControls(resources);
        initValidators(resources);
        initValidation(resources);
    }

    private void initControls(ResourceBundle resources) {
        address.setText(SETTINGS.getServerAddress().toExternalForm());
        address.focusedProperty().addListener((observable, oldValue, newValue) -> {
            // when focus lost
            if (!newValue) {
                // text is valid
                if (addressValidator.apply(address, address.getText()).getErrors().isEmpty()) {
                    // store server url address
                    try {
                        SETTINGS.setServerAddress(new URL(address.getText()));
                    } catch (MalformedURLException e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, e.getLocalizedMessage(), ButtonType.CLOSE);
                        alert.showAndWait();
                    }
                }
            }
        });
    }

    private void initValidators(ResourceBundle resources) {
        addressValidator = Validator.createRegexValidator(
                resources.getString(Resources.Strings.VALIDATION_CONNECTION_ADDRESS),
                "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",
                Severity.ERROR
        );
    }

    private void initValidation(ResourceBundle resources) {
        validationSupport.initInitialDecoration();

        validationSupport.registerValidator(address, addressValidator);
    }
}
