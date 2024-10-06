import Constants from "../../../common/js/Constants";
import LocalizationHandler from "../../../common/js/LocalizationHandler";
import { isBlank } from "../../../common/js/Utils";
import ValidationResult from "../../../common/js/validation/ValidationResult";
import localizationData from "./account_validation_localization.json";

export const validateEmail = (email) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    if (isBlank(email)) {
        return new ValidationResult(false, localizationHandler.get("must-not-be-blank"));
    }

    if (!email.match("^\\s*[\\w\\-\\+_]+(\\.[\\w\\-\\+_]+)*\\@[\\w\\-\\+_]+\\.[\\w\\-\\+_]+(\\.[\\w\\-\\+_]+)*\\s*$")) {
        return new ValidationResult(false, localizationHandler.get("invalid-email"));
    }

    return new ValidationResult();
}

export const validateFilled = (value) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    if (isBlank(value)) {
        return new ValidationResult(false, localizationHandler.get("must-not-be-blank"));
    }

    return new ValidationResult();
}

export const validateUsername = (username) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    if (username.length < Constants.MIN_USERNAME_LENGTH) {
        return new ValidationResult(false, localizationHandler.get("username-too-short"));
    }

    if (username.length > Constants.MAX_USERNAME_LENGTH) {
        return new ValidationResult(false, localizationHandler.get("username-too-long"));
    }

    return new ValidationResult();
}

export const validatePassword = (password) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    if (password.length < Constants.MIN_PASSWORD_LENGTH) {
        return new ValidationResult(false, localizationHandler.get("password-too-short"));
    }

    if (password.length > Constants.MAX_PASSWORD_LENGTH) {
        return new ValidationResult(false, localizationHandler.get("password-too-long"));
    }

    return new ValidationResult();
}

export const validateConfirmPassword = (confirmPassword, password) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    if (confirmPassword !== password) {
        return new ValidationResult(false, localizationHandler.get("incorrect-confirm-password"));
    }

    return new ValidationResult();
}