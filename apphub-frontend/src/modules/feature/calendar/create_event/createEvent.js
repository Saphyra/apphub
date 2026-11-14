import LocalizationHandler from "common/js/LocalizationHandler";
import localizationData from "./create_event_page_localization.json";
import validateEventRequest from "../common/event/validateEventRequest";
import Stream from "common/js/collection/Stream";
import NotificationService from "common/js/notification/NotificationService";
import { CALENDAR_CREATE_EVENT, CALENDAR_CREATE_LABEL, CALENDAR_PAGE, CALENDAR_SHARE_OBJECT } from "../CalendarEndpoints";
import { GET_OWN_USER_ID } from "common/js/GenericEndpoints";

const localizationHandler = new LocalizationHandler(localizationData);

async function createEvent(payload, existingLabels, setDisplaySpinner, newLabels, shareWith) {
    if (!validateEventRequest(payload, true)) {
        return;
    }

    const newLabelIds = await createLabels();

    payload.labels = new Stream(existingLabels)
        .addAll(newLabelIds)
        .toMap(l => l.labelId, l => l.userId);

    const response = await CALENDAR_CREATE_EVENT.createRequest(payload)
        .send(setDisplaySpinner);
    const eventId = response.value;

    if (shareWith.length > 0) {
        await shareWithUsers(setDisplaySpinner, shareWith, eventId);
    }

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

    async function shareWithUsers(setDisplaySpinner, shareWith, eventId) {
        const owner = await GET_OWN_USER_ID.createRequest()
            .send(setDisplaySpinner);
        const ownerId = owner.value;

        await Promise.all(shareWith.map(user => shareWithUser(ownerId, eventId, user.userId, user.grants)));

        async function shareWithUser(ownerId, eventId, shareWithUserId, selectedGrants) {
            const payload = {
                sharedWith: shareWithUserId,
                owner: ownerId,
                parent: ownerId,
                objectId: eventId,
                type: "EVENT",
                grants: selectedGrants
            }

            return CALENDAR_SHARE_OBJECT.createRequest(payload)
                .send(setDisplaySpinner);
        }
    }
}

export default createEvent;