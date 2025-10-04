import ConfirmationDialogData from "../../../../common/component/confirmation_dialog/ConfirmationDialogData";
import Button from "../../../../common/component/input/Button";
import { CALENDAR_DELETE_EVENT } from "../../../../common/js/dao/endpoints/CalendarEndpoints";
import LocalizationHandler from "../../../../common/js/LocalizationHandler";
import localizationData from "./delete_event_localization.json";

const localizationHandler = new LocalizationHandler(localizationData);

function confirmEventDeletion(setConfirmationDialogData, eventId, eventTitle, setDisplaySpinner, setSelectedEvent, callback = () => {}) {
    setConfirmationDialogData(new ConfirmationDialogData(
        "calendar-event-delete-confirmation",
        localizationHandler.get("delete-event-confirmation-title"),
        localizationHandler.get("delete-event-confirmation-detail", { eventTitle: eventTitle }),
        [
            <Button
                key="delete"
                id="calendar-event-delete-confirmation-button"
                label={localizationHandler.get("delete")}
                onclick={() => deleteEvent()}
            />,
            <Button
                key="cancel"
                id="calendar-event-delete-cancel-button"
                label={localizationHandler.get("cancel")}
                onclick={() => setConfirmationDialogData(null)}
            />
        ]
    ));

    async function deleteEvent() {
        await CALENDAR_DELETE_EVENT.createRequest(null, { eventId: eventId })
            .send(setDisplaySpinner);

        setConfirmationDialogData(null);
        setSelectedEvent(null)
        callback();
    }
}

export default confirmEventDeletion;