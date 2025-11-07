import { CALENDAR_CREATE_OCCURRENCE } from "../../../common/js/dao/endpoints/CalendarEndpoints";
import NotificationService from "../../../common/js/notification/NotificationService";
import { isBlank } from "../../../common/js/Utils";
import { OccurrenceStatus } from "../common/OccurrenceStatus";

async function createOccurrence(eventId, date, setDate, setDisplaySpinner, localizationHandler) {
    if (isBlank(date)) {
        NotificationService.showError(localizationHandler.get("no-date-selected"))
        return;
    }

    const payload = {
        date: date.format(),
        status: OccurrenceStatus.PENDING,
        remindMeBeforeDays: 0,
        note: "",
        reminded: false
    }

    await CALENDAR_CREATE_OCCURRENCE.createRequest(payload, { eventId: eventId })
        .send(setDisplaySpinner);

    NotificationService.showSuccess(localizationHandler.get("occurrence-created"));
    setDate(null);
}

export default createOccurrence;