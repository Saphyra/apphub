import Stream from "../../../common/js/collection/Stream";
import { CALENDAR_CREATE_EVENT, CALENDAR_CREATE_LABEL, CALENDAR_PAGE } from "../../../common/js/dao/endpoints/CalendarEndpoints";
import LocalizationHandler from "../../../common/js/LocalizationHandler";
import NotificationService from "../../../common/js/notification/NotificationService";
import validateEventRequest from "../common/event/validateEventRequest";
import localizationData from "./create_event_page_localization.json";

const localizationHandler = new LocalizationHandler(localizationData);

async function createEvent(payload, existingLabels, setDisplaySpinner, newLabels) {
    if (!validateEventRequest(payload)) {
        return;
    }

    const newLabelIds = await createLabels();

    payload.labels = new Stream(existingLabels)
        .addAll(newLabelIds)
        .toList()

    await CALENDAR_CREATE_EVENT.createRequest(payload)
        .send(setDisplaySpinner);

    NotificationService.storeSuccessText(localizationHandler.get("event-created"));
    window.location.href = CALENDAR_PAGE;

    async function createLabels() {
        return await Promise.all(newLabels.map(label => createLabel(label)));

        async function createLabel(label) {
            return CALENDAR_CREATE_LABEL.createRequest({ value: label })
                .send(setDisplaySpinner)
                .then(response => response.value);
        }
    }
}

export default createEvent;