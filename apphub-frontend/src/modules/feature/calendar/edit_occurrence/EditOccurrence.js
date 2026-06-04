import { CALENDAR_EDIT_OCCURRENCE } from "../CalendarEndpoints";
import validateOccurrenceRequest from "./ValidateOccurrenceRequest";
import NotificationService from "common/js/notification/NotificationService";

async function save({
    localizationHandler,
    date,
    time,
    status,
    note,
    remindMeBeforeDays,
    reminded,
    eventId,
    occurrenceId,
    setDisplaySpinner,
    backUrl
}) {
    const payload = {
        date: date,
        time: time,
        status: status,
        note: note,
        remindMeBeforeDays: remindMeBeforeDays,
        reminded: reminded
    };

    if (!validateOccurrenceRequest(localizationHandler, payload)) {
        return;
    }

    await CALENDAR_EDIT_OCCURRENCE.createRequest(payload, { eventId: eventId, occurrenceId: occurrenceId })
        .send(setDisplaySpinner);

    NotificationService.storeSuccessText(localizationHandler.get("saved"));
    window.location.href = backUrl;
}

export default save;