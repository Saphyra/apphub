import LocalizationHandler from "common/js/LocalizationHandler";
import localizationData from "./validation_localization.json";
import { MAX_LIST_ITEM_CONTENT_LENGTH } from "../NotebookConstants";
import ValidationResult from "common/js/validation/ValidationResult";

const validateText = (content) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    if (content.length > MAX_LIST_ITEM_CONTENT_LENGTH) {
        return new ValidationResult(false, localizationHandler.get("content-too-long"));
    }

    return new ValidationResult(true);
}


export default validateText;