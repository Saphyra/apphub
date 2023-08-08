import MapStream from "../collection/MapStream";
import ValidatedField from "./ValidatedField";
import Constants from "../Constants";
import Utils from "../Utils";
import ValidationResult from "./ValidationResult"

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
        case ValidatedField.CHARACTER_NAME:
            return validateCharacterName(value, localizationHandler);
            case ValidatedField.GAME_NAME:
                return validateGameName(value, localizationHandler);
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

const validateCharacterName = (characterName, localizationHandler) => {
    if (characterName.length < Constants.MIN_CHARACTER_NAME_LENGTH) {
        return new ValidationResult(false, localizationHandler.get("character-name-too-short"));
    }

    if (characterName.length > Constants.MAX_CHARACTER_NAME_LENGTH) {
        return new ValidationResult(false, localizationHandler.get("character-name-too-long"));
    }

    return new ValidationResult();
}

const validateGameName = (gameName, localizationHandler) => {
    if (gameName.length < Constants.MIN_GAME_NAME_LENGTH) {
        return new ValidationResult(false, localizationHandler.get("game-name-too-short"));
    }

    if (gameName.length > Constants.MAX_GAME_NAME_LENGTH) {
        return new ValidationResult(false, localizationHandler.get("game-name-too-long"));
    }

    return new ValidationResult();
}

export default validate;