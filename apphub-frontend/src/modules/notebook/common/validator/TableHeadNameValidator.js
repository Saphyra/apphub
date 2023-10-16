import Utils from "../../../../common/js/Utils"
import ValidationResult from "../../../../common/js/validation/ValidationResult";
import localizationData from "./validation_localization.json";
import LocalizationHandler from "../../../../common/js/LocalizationHandler";
import Stream from "../../../../common/js/collection/Stream";

const validateTableHeadNames = (tableHeads) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    if (new Stream(tableHeads).map(tableHead => tableHead.content).anyMatch(tableHeadName => Utils.isBlank(tableHeadName))) {
        return new ValidationResult(false, localizationHandler.get("table-head-name-blank"));
    }

    return new ValidationResult(true);
}

export default validateTableHeadNames;