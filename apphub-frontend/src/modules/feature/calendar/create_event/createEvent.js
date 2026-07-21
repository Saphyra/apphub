import LocalizationHandler from "common/js/LocalizationHandler";
import localizationData from "./create_event_page_localization.json";
import validateEventRequest from "../common/event/validateEventRequest";
import Stream from "common/js/collection/Stream";
import NotificationService from "common/js/notification/NotificationService";
import { CALENDAR_CREATE_EVENT, CALENDAR_CREATE_LABEL, CALENDAR_PAGE } from "../CalendarEndpoints";

const localizationHandler = new LocalizationHandler(localizationData);

async function createEvent(payload, existingLabels, setDisplaySpinner, newLabels) {
    if (!validateEventRequest(payload, true)) {
        return;
    }

    const newLabelIds = await createLabels();

    payload.labels = new Stream(existingLabels)
        .addAll(newLabelIds)
        .toMap(l => l.labelId, l => l.userId);

    await CALENDAR_CREATE_EVENT.createRequest(payload)
        .send(setDisplaySpinner);

    NotificationService.storeSuccessText(localizationHandler.get("event-created"));
    window.location.href = CALENDAR_PAGE;

    async function createLabels() {
        return await Promise.all(newLabels.map(label => createLabel(label.label)));

        async function createLabel(label) {
            return CALENDAR_CREATE_LABEL.createRequest({ value: label })
                .send(setDisplaySpinner)
                .then(response => { return { labelId: response.labelId, userId: response.userId } });
        }
    }
}

export default createEvent;