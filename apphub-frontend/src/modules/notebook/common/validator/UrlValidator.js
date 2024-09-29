import LocalizationHandler from "../../../../common/js/LocalizationHandler";
import { isBlank } from "../../../../common/js/Utils";
import ValidationResult from "../../../../common/js/validation/ValidationResult";
import localizationData from "./validation_localization.json";

const validateUrl = (url) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    if (isBlank(url)) {
        return new ValidationResult(false, localizationHandler.get("url-blank"));
    }

    return new ValidationResult(true);
}

export default validateUrl;