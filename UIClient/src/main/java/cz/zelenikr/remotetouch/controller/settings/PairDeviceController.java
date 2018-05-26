package cz.zelenikr.remotetouch.controller.settings;

import cz.zelenikr.remotetouch.Resources;
import cz.zelenikr.remotetouch.manager.SettingsManager;
import cz.zelenikr.remotetouch.controller.Controller;
import cz.zelenikr.remotetouch.controller.Validateable;
import cz.zelenikr.remotetouch.validation.Validators;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Provides changing device pairing settings.
 *
 * @author Roman Zelenik
 */
public class PairDeviceController implements Controller, Initializable, Validateable {

    private static final SettingsManager SETTINGS = SettingsManager.getInstance();
    private final ValidationSupport validationSupport = new ValidationSupport();
    private final BooleanProperty changedProperty = new SimpleBooleanProperty(false);

    @FXML
    private TextField deviceName;
    @FXML
    private TextField pairKey;

    private Validator<String> deviceNameValidator, pairKeyValidator;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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

    public ReadOnlyBooleanProperty getChangedProperty() {
        return changedProperty;
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

                    notifyChanged();
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

                    notifyChanged();
                }
            }
        });
    }

    private void initValidators(ResourceBundle resources) {
        deviceNameValidator = Validators.createDeviceNameValidator(resources.getString(Resources.Strings.VALIDATION_DEVICE_NAME));
        pairKeyValidator = Validators.createPairKeyValidator(resources.getString(Resources.Strings.VALIDATION_DEVICE_PAIR_KEY));
    }

    private void initValidation(ResourceBundle resources) {
        validationSupport.initInitialDecoration();

        validationSupport.registerValidator(deviceName, deviceNameValidator);
        validationSupport.registerValidator(pairKey, pairKeyValidator);
    }

    private void notifyChanged() {
        changedProperty.setValue(true);
    }
}
