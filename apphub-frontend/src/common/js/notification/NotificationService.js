import errorCodes from "./notification_translations.json";
import LocalizationHandler from "../LocalizationHandler";
import { toast } from 'react-toastify';

const KEY_SUCCESS_TEXT = "successText";

const localizationHandler = new LocalizationHandler(errorCodes);

const showError = (message, timeout = 0) => {
    setTimeout(() => {
        toast.error(
            message,
            {
                position: toast.POSITION.TOP_LEFT
            }
        );
    }, timeout);
}

const showSuccess = (message, timeout = 0) => {
    setTimeout(() => {
        toast.success(
            message,
            {
                position: toast.POSITION.TOP_LEFT
            }
        );
    }, timeout);
}

const storeSuccessText = (text) => {
    sessionStorage[KEY_SUCCESS_TEXT] = text;
}

const displayStoredMessages = () => {
    if (sessionStorage.errorCode) {
        showError(localizationHandler.get(sessionStorage.errorCode), 500);
        delete sessionStorage.errorCode;
    }

    if (sessionStorage.successCode) {
        showSuccess(localizationHandler.get(sessionStorage.successCode), 500);
        delete sessionStorage.successCode;
    }

    if (sessionStorage.errorText) {
        showError(sessionStorage.errorText, 500);
        delete sessionStorage.errorText;
    }

    if (sessionStorage.successText) {
        showSuccess(sessionStorage.successText, 500);
        delete sessionStorage.successText;
    }
}

const NotificationService = {
    showError: showError,
    showErrorCode: (errorCode, params = {}) => showError(localizationHandler.get(errorCode, params)),
    showSuccess: showSuccess,
    showSuccessCode: (errorCode, params) => showSuccess(localizationHandler.get(errorCode, params)),
    displayStoredMessages: displayStoredMessages,
    storeSuccessText: storeSuccessText,
}

export default NotificationService;