import LocalizationHandler from "../../../../common/js/LocalizationHandler";
import Utils from "../../../../common/js/Utils";
import ValidationResult from "../../../../common/js/validation/ValidationResult";
import localizationData from "./validation_localization.json";

const validatePinGroupName =(pinGroupName) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    if(Utils.isBlank(pinGroupName)){
        return new ValidationResult(false, localizationHandler.get("pin-group-name-blank"));
    }

    return new ValidationResult(true);
}

export default validatePinGroupName;