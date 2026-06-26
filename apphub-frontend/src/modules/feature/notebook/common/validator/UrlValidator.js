import LocalizationHandler from "common/js/LocalizationHandler";
import localizationData from "./validation_localization.json";
import { isBlank } from "common/js/Utils";
import ValidationResult from "common/js/validation/ValidationResult";
import { MAX_LIST_ITEM_CONTENT_LENGTH } from "../NotebookConstants";

const validateUrl = (url) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    if (isBlank(url)) {
        return new ValidationResult(false, localizationHandler.get("url-blank"));
    }

    if(url.length > MAX_LIST_ITEM_CONTENT_LENGTH) {
        return new ValidationResult(false, localizationHandler.get("content-too-long"));
    }

    return new ValidationResult(true);
}

export default validateUrl;