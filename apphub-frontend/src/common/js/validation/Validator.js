import MapStream from "../collection/MapStream";
import ValidatedField from "./ValidatedField";
import Constants from "../Constants";
import Utils from "../Utils";

const validate = (fields, localizationHandler) => {
    return new MapStream(fields)
        .map((field, value) => validateField(field, value, fields, localizationHandler))
        .toObject();
}

const validateField = (field, value, fields, localizationHandler) => {
    switch (field) {
        case ValidatedField.USERNAME:
            return validateUsername(value, localizationHandler);
        case ValidatedField.EMAIL:
            return validateEmail(value, localizationHandler);
        case ValidatedField.PASSWORD:
            return validatePassword(value, localizationHandler);
        case ValidatedField.CONFIRM_PASSWORD:
            return validateConfirmPassword(value, fields[ValidatedField.PASSWORD], localizationHandler);
        default:
            Utils.throwException("IllegalArgument", field + " is not a validated field.");
    }
}

const validateUsername = (usename, localizationHandler) => {
    if (usename.length < Constants.MIN_USERNAME_LENGTH) {
        return new ValidationResult(false, localizationHandler.get("username-too-short"));
    }

    if (usename.length > Constants.MAX_USERNAME_LENGTH) {
        return new ValidationResult(false, localizationHandler.get("username-too-long"));
    }

    return new ValidationResult();
}

const validateEmail = (email, localizationHandler) => {
    if (!email.match("^\\s*[\\w\\-\\+_]+(\\.[\\w\\-\\+_]+)*\\@[\\w\\-\\+_]+\\.[\\w\\-\\+_]+(\\.[\\w\\-\\+_]+)*\\s*$")) {
        return new ValidationResult(false, localizationHandler.get("invalid-email"));
    }

    return new ValidationResult();
}

const validatePassword = (password, localizationHandler) => {
    if (password.length < Constants.MIN_PASSWORD_LENGTH) {
        return new ValidationResult(false, localizationHandler.get("password-too-short"));
    }

    if (password.length > Constants.MAX_PASSWORD_LENGTH) {
        return new ValidationResult(false, localizationHandler.get("password-too-long"));
    }

    return new ValidationResult();
}

const validateConfirmPassword = (confirmPassword, password, localizationHandler) => {
    if(confirmPassword !== password){
        return new ValidationResult(false, localizationHandler.get("incorrect-confirm-password"));
    }

    return new ValidationResult();
}

const ValidationResult = class {
    constructor(valid = true, message) {
        this.valid = valid;
        this.message = message;
    }
}

export default validate;