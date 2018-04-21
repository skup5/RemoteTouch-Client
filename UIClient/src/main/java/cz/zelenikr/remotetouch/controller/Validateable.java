package cz.zelenikr.remotetouch.controller;

import org.controlsfx.validation.ValidationSupport;

/**
 * @author Roman Zelenik
 */
public interface Validateable {

    ValidationSupport getValidationSupport();

    /**
     * @return true if all inner validateable controls are valid
     */
    boolean isValid();
}
