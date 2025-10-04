import NotificationService from "../../../common/js/notification/NotificationService";
import { isBlank } from "../../../common/js/Utils";

function validateOccurrenceRequest(localizationHandler, request) {
    if(isBlank(request.date)) {
        NotificationService.showError(localizationHandler.get("empty-date"));
        return false;
    }

    if (request.remindMeBeforeDays < 0) {
        NotificationService.showError(localizationHandler.get("remind-me-before-days-too-low"));
        return false;
    }

    return true;
}

export default validateOccurrenceRequest;