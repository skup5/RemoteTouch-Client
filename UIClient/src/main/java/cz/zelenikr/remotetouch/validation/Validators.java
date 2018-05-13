package cz.zelenikr.remotetouch.validation;

import org.controlsfx.validation.Severity;
import org.controlsfx.validation.Validator;

/**
 * Validators of user inputs.
 *
 * @author Roman Zelenik
 */
public class Validators {

    public static Validator<String> createPasswordValidator(final String message) {
        return Validator.createRegexValidator(message, "(^.{5,})", Severity.ERROR);
    }

    public static Validator<String> createUrlValidator(final String message) {
        return Validator.createRegexValidator(
                message,
                "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",
                Severity.ERROR
        );
    }

    public static Validator<String> createDeviceNameValidator(final String message) {
        return Validator.createEmptyValidator(message);
    }

    public static Validator<String> createPairKeyValidator(final String message) {
        return Validator.createEmptyValidator(message);
    }

    private Validators() {
    }
}
