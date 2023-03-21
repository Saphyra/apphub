import errorCodes from "./notification_translations.json";
import LocalizationHandler from "../LocalizationHandler";
import { toast } from 'react-toastify';

const localizationHandler = new LocalizationHandler(errorCodes);

const showError = (message) => {
    toast.error(
        message,
        {
            position: toast.POSITION.TOP_LEFT
        }
    );
}

const showSuccess = (message) => {
    toast.success(
        message,
        {
            position: toast.POSITION.TOP_LEFT
        }
    );
}

const displayStoredMessages = () => {
    console.log("errorCode", sessionStorage.errorCode);
    console.log("successCode", sessionStorage.successCode);
    console.log("errorText", sessionStorage.errorText);
    console.log("successText", sessionStorage.successText);

    if (sessionStorage.errorCode) {
        showError(localizationHandler.get(sessionStorage.errorCode));
        delete sessionStorage.errorCode;
    }

    if (sessionStorage.successCode) {
        showSuccess(localizationHandler.get(sessionStorage.successCode));
        delete sessionStorage.successCode;
    }

    if (sessionStorage.errorText) {
        showError(sessionStorage.errorText);
        delete sessionStorage.errorText;
    }

    if (sessionStorage.successText) {
        showSuccess(sessionStorage.successText);
        delete sessionStorage.successText;
    }
}

const NotificationService = {
    showError: showError,
    showErrorCode: (errorCode, params = {}) => showError(localizationHandler.get(errorCode, params)),
    showSuccess: showSuccess,
    showSuccessCode: (errorCode, params) => showSuccess(localizationHandler.get(errorCode, params)),
    displayStoredMessages: displayStoredMessages
}

export default NotificationService;