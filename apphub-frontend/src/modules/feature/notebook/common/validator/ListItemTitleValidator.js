import LocalizationHandler from "common/js/LocalizationHandler";
import localizationData from "./validation_localization.json";
import { isBlank } from "common/js/Utils";
import ValidationResult from "common/js/validation/ValidationResult";
import { MAX_LIST_ITEM_TITLE_LENGTH } from "../NotebookConstants";

const validateListItemTitle = (listItemTitle) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    if (isBlank(listItemTitle)) {
        return new ValidationResult(false, localizationHandler.get("list-item-title-blank"));
    }

    if (listItemTitle.length > MAX_LIST_ITEM_TITLE_LENGTH) {
        return new ValidationResult(false, localizationHandler.get("list-item-title-too-long"));
    }


    return new ValidationResult(true);
}

export default validateListItemTitle;