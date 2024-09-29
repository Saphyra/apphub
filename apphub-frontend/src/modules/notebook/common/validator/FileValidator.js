import Constants from "../../../../common/js/Constants";
import LocalizationHandler from "../../../../common/js/LocalizationHandler";
import { hasValue } from "../../../../common/js/Utils";
import ValidationResult from "../../../../common/js/validation/ValidationResult";
import localizationData from "./validation_localization.json";

const validateFile = (fileData) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    if(!hasValue(fileData)){
        return new ValidationResult(false, localizationHandler.get("file-not-selected"));
    }

    if(fileData.size > Constants.FILE_SIZE_LIMIT){
        return new ValidationResult(false, localizationHandler.get("file-too-large"));
    }

    return new ValidationResult(true);
}

export default validateFile;