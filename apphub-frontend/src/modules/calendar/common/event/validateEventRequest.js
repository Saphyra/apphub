import LocalDate from "../../../../common/js/date/LocalDate";
import LocalizationHandler from "../../../../common/js/LocalizationHandler";
import NotificationService from "../../../../common/js/notification/NotificationService";
import { isBlank } from "../../../../common/js/Utils";
import { REPETITION_TYPE_DAYS_OF_MONTH, REPETITION_TYPE_DAYS_OF_WEEK, REPETITION_TYPE_EVERY_X_DAYS, REPETITION_TYPE_ONE_TIME } from "../repetition_type/RepetitionType";
import localizationData from "./event_localization.json";

const localizationHandler = new LocalizationHandler(localizationData);

function validateEventRequest(payload) {
     if (payload.repetitionType !== REPETITION_TYPE_ONE_TIME) {
        if (isBlank(payload.endDate)) {
            NotificationService.showError(localizationHandler.get("empty-end-date"));
            return false;
        }

        if (LocalDate.parse(payload.endDate).isBefore(LocalDate.parse(payload.startDate))) {
            NotificationService.showError(localizationHandler.get("end-date-before-start-date"));
            return false;
        }
    }

    if (payload.repeatForDays < 1) {
        NotificationService.showError(localizationHandler.get("repeat-for-days-too-low"));
        return false;
    }

    if (payload.repetitionType === REPETITION_TYPE_EVERY_X_DAYS && payload.repetitionData < 1) {
        NotificationService.showError("repeat-every-x-days-too-low");
        return false;
    }

    if (payload.repetitionType === REPETITION_TYPE_DAYS_OF_WEEK || payload.repetitionType === REPETITION_TYPE_DAYS_OF_MONTH) {
        if (payload.repeatForDays.length === 0) {
            NotificationService.showError(localizationHandler.get("no-days-defined"));
            return false;
        }
    }

    if (isBlank(payload.title)) {
        NotificationService.showError(localizationHandler.get("blank-title"));
        return false;
    }

    if (payload.remindMeBeforeDays < 0) {
        NotificationService.showError(localizationHandler.get("remind-me-before-days-too-low"));
        return false;
    }

    return true;
}

export default validateEventRequest;