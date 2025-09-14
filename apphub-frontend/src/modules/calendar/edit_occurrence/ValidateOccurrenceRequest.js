import NotificationService from "../../../common/js/notification/NotificationService";

function validateOccurrenceRequest(localizationHandler, request) {
    if (request.remindMeBeforeDays < 0) {
        NotificationService.showError(localizationHandler.get("remind-me-before-days-too-low"));
        return false;
    }

    return true;
}

export default validateOccurrenceRequest;