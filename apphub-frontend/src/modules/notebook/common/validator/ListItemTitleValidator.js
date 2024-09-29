import ValidationResult from "../../../../common/js/validation/ValidationResult";
import localizationData from "./validation_localization.json";
import LocalizationHandler from "../../../../common/js/LocalizationHandler";
import { isBlank } from "../../../../common/js/Utils";

const validateListItemTitle = (listItemTitle) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    if (isBlank(listItemTitle)) {
        return new ValidationResult(false, localizationHandler.get("list-item-title-blank"));
    }

    return new ValidationResult(true);
}

export default validateListItemTitle;