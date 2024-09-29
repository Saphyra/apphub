import LocalizationHandler from "../../../../common/js/LocalizationHandler";
import { isBlank } from "../../../../common/js/Utils";
import Stream from "../../../../common/js/collection/Stream";
import ValidationResult from "../../../../common/js/validation/ValidationResult";
import ColumnType from "../table/row/column/type/ColumnType";
import localizationData from "./validation_localization.json";

const validateColumnData = (rows) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    return new Stream(rows)
        .flatMap(row => new Stream(row.columns))
        .map(column => validateColumn(column, localizationHandler))
        .filter(validationResult => !validationResult.valid)
        .findFirst()
        .orElse(new ValidationResult());
}

const validateColumn = (column, localizationHandler) => {
    switch (column.columnType) {
        case ColumnType.LINK:
            if(isBlank(column.data.label)){
                return new ValidationResult(false, localizationHandler.get("link-label-blank"));
            }
        default:
            return new ValidationResult();
    }
}

export default validateColumnData;