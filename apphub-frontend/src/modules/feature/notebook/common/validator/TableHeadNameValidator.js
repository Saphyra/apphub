import LocalizationHandler from "common/js/LocalizationHandler";
import localizationData from "./validation_localization.json";
import Stream from "common/js/collection/Stream";
import { isBlank } from "common/js/Utils";
import ValidationResult from "common/js/validation/ValidationResult";

const validateTableHeadNames = (tableHeads) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    if (new Stream(tableHeads).map(tableHead => tableHead.content).anyMatch(tableHeadName => isBlank(tableHeadName))) {
        return new ValidationResult(false, localizationHandler.get("table-head-name-blank"));
    }

    return new ValidationResult(true);
}

export default validateTableHeadNames;