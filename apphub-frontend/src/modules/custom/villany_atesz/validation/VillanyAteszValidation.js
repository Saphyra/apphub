import LocalizationHandler from "../../../../common/js/LocalizationHandler";
import { isBlank } from "../../../../common/js/Utils";
import Stream from "../../../../common/js/collection/Stream";
import ValidationResult from "../../../../common/js/validation/ValidationResult"
import localizationData from "./villany_atesz_validation_localization.json";

const localizationHandler = new LocalizationHandler(localizationData);

export const validateContact = (contact) => {
    if (isBlank(contact.name)) {
        return new ValidationResult(false, localizationHandler.get("blank-name"))
    }

    return new ValidationResult();
}

export const validateAcquiredItems = (items) => {
    if (validateList(items, item => isBlank(item.stockCategoryId))) {
        return new ValidationResult(false, localizationHandler.get("no-category-selected"));
    }

    if (validateList(items, item => isBlank(item.stockItemId))) {
        return new ValidationResult(false, localizationHandler.get("no-item-selected"));
    }

    return new ValidationResult();
}

export const validateOverviewAmount = (amount) => {
    if (amount === 0) {
        return new ValidationResult(false, localizationHandler.get("amount-zero"));
    }

    return new ValidationResult();
}

const validateList = (items, predicate) => {
    return new Stream(items)
        .anyMatch(predicate);
}