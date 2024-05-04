import LocalizationHandler from "../../../../common/js/LocalizationHandler";
import Utils from "../../../../common/js/Utils"
import Stream from "../../../../common/js/collection/Stream";
import ValidationResult from "../../../../common/js/validation/ValidationResult"
import localizationData from "./villany_atesz_validation_localization.json";

const localizationHandler = new LocalizationHandler(localizationData);

export const validateContact = (contact) => {
    if (Utils.isBlank(contact.code)) {
        return new ValidationResult(false, localizationHandler.get("blank-code"))
    }

    if (Utils.isBlank(contact.name)) {
        return new ValidationResult(false, localizationHandler.get("blank-name"))
    }

    return new ValidationResult();
}

export const validateAcquiredItems = (items) => {
    if (validateList(items, item => Utils.isBlank(item.stockCategoryId))) {
        return new ValidationResult(false, localizationHandler.get("no-category-selected"));
    }

    if (validateList(items, item => Utils.isBlank(item.stockItemId))) {
        return new ValidationResult(false, localizationHandler.get("no-item-selected"));
    }

    return new ValidationResult();
}

export const validateOverviewAmount = (amount)=>{
    if(amount === 0){
        return new ValidationResult(false, localizationHandler.get("amount-zero"));
    }

    return new ValidationResult();
}

const validateList = (items, predicate) => {
    return new Stream(items)
        .anyMatch(predicate);
}