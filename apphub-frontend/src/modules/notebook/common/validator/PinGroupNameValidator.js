import Constants from "../../../../common/js/Constants";
import LocalizationHandler from "../../../../common/js/LocalizationHandler";
import Utils from "../../../../common/js/Utils";
import ValidationResult from "../../../../common/js/validation/ValidationResult";
import localizationData from "./validation_localization.json";

const validatePinGroupName = (pinGroupName) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    if (Utils.isBlank(pinGroupName)) {
        return new ValidationResult(false, localizationHandler.get("pin-group-name-blank"));
    }

    if (pinGroupName.length > Constants.MAX_PIN_GROUP_NAME_LENGTH) {
        return new ValidationResult(false, localizationHandler.get("pin-group-name-too-long"));
    }

    return new ValidationResult(true);
}

export default validatePinGroupName;