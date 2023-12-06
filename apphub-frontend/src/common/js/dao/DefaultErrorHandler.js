import Constants from "../Constants";
import NotificationKey from "../notification/NotificationKey";
import NotificationService from "../notification/NotificationService";
import ErrorHandler from "./ErrorHandler";

const getDefaultErrorHandler = () => {
    return new ErrorHandler(
        () => true,
        (response) => {
            if (isErrorResponse(response.body)) {
                const errorResponse = JSON.parse(response.body);

                switch (errorResponse.errorCode) {
                    case "SESSION_EXPIRED":
                    case "NO_SESSION_AVAILABLE":
                        sessionStorage.errorCode = NotificationKey.NO_VALID_SESSION;
                        window.location.href = Constants.INDEX_PAGE + "?redirect=/" + (window.location.pathname + window.location.search).substr(1);
                        break;
                    default:
                        NotificationService.showErrorCode(errorResponse.errorCode, errorResponse.params);
                }

            } else if (response.status == 0) {
                console.log("Connection failed");
            } else {
                NotificationService.showError("Error response from BackEnd: " + response.toString());
            }

            function isErrorResponse(responseBody) {
                try {
                    if (responseBody === null || responseBody.length === 0) {
                        console.log("Empty response body");
                        return false;
                    }

                    const errorResponse = JSON.parse(responseBody);

                    return errorResponse.errorCode !== undefined
                        && errorResponse.params !== undefined;
                } catch (e) {
                    console.log(e);
                    return false;
                }
            }
        }
    );
}

export default getDefaultErrorHandler;