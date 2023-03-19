import errorCodes from "./error_codes.json";
import LocalizationHandler from "../LocalizationHandler";

const localizationHandler = new LocalizationHandler(errorCodes);

const showError = (message) => {
    //TODO
}

const showSuccess = (message) => {
    //TODO
}

const NotificationService = {
    showError: showError,
    showErrorCode: (errorCode, params = {}) => showError(localizationHandler.get(errorCode, params)),
    showSuccess: showSuccess,
    showSuccessCode: (errorCode, params) => showSuccess(localizationHandler.get(errorCode, params))
}

export default NotificationService;