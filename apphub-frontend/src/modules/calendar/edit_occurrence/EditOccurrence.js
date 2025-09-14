import { CALENDAR_EDIT_OCCURRENCE, CALENDAR_PAGE } from "../../../common/js/dao/endpoints/CalendarEndpoints";
import NotificationService from "../../../common/js/notification/NotificationService";
import validateOccurrenceRequest from "./ValidateOccurrenceRequest";

async function save({
    localizationHandler,
    date,
    time,
    status,
    note,
    remindMeBeforeDays,
    reminded,
    occurrenceId,
    setDisplaySpinner
}) {
    if (!validateOccurrenceRequest(localizationHandler, payload)) {
        return;
    }

    const payload = {
        date: date,
        time: time,
        status: status,
        note: note,
        remindMeBeforeDays: remindMeBeforeDays,
        reminded: reminded
    };

    await CALENDAR_EDIT_OCCURRENCE.createRequest(payload, { occurrenceId: occurrenceId })
        .send(setDisplaySpinner);

    NotificationService.storeSuccessText(localizationHandler.get("saved"));
    window.location.href = CALENDAR_PAGE;
}

export default save;