import Stream from "../../../common/js/collection/Stream";
import { CALENDAR_CREATE_LABEL, CALENDAR_EDIT_EVENT, CALENDAR_PAGE } from "../../../common/js/dao/endpoints/CalendarEndpoints";
import LocalizationHandler from "../../../common/js/LocalizationHandler";
import NotificationService from "../../../common/js/notification/NotificationService";
import validateEventRequest from "../common/event/validateEventRequest";
import localizationData from "./calendar_edit_event_page_localization.json";

const localizationHandler = new LocalizationHandler(localizationData);

async function saveEvent(eventId, payload, existingLabels, setDisplaySpinner, newLabels) {
    if (!validateEventRequest(payload)) {
        return;
    }

    const newLabelIds = await createLabels();

    payload.labels = new Stream(existingLabels)
        .addAll(newLabelIds)
        .toList()

    await CALENDAR_EDIT_EVENT.createRequest(payload, { eventId: eventId })
        .send(setDisplaySpinner);

    NotificationService.showSuccess(localizationHandler.get("event-saved"));

    async function createLabels() {
        return await Promise.all(newLabels.map(label => createLabel(label)));

        async function createLabel(label) {
            return CALENDAR_CREATE_LABEL.createRequest({ value: label })
                .send(setDisplaySpinner)
                .then(response => response.value);
        }
    }
}

export default saveEvent;