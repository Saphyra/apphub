import ConfirmationDialogData from "../../../../common/component/confirmation_dialog/ConfirmationDialogData";
import Button from "../../../../common/component/input/Button";
import { CALENDAR_DELETE_OCCURRENCE } from "../../../../common/js/dao/endpoints/CalendarEndpoints";
import LocalizationHandler from "../../../../common/js/LocalizationHandler";
import localizationData from "./delete_occurrence_localization.json";

const localizationHandler = new LocalizationHandler(localizationData);

function confirmOccurrenceDeletion(
    setConfirmationDialogData,
    occurrenceId, 
    eventTitle,
    occurrenceDate,
    setDisplaySpinner,
    setSelectedEvent,
    callback
) {
    setConfirmationDialogData(new ConfirmationDialogData(
        "calendar-occurrence-delete-confirmation",
        localizationHandler.get("delete-occurrence-confirmation-title"),
        localizationHandler.get("delete-occurrence-confirmation-detail", { eventTitle: eventTitle, occurrenceDate: occurrenceDate }),
        [
            <Button
                key="delete"
                id="calendar-occurrence-delete-confirmation-button"
                label={localizationHandler.get("delete")}
                onclick={() => deleteOccurrence()}
            />,
            <Button
                key="cancel"
                id="calendar-occurrence-delete-cancel-button"
                label={localizationHandler.get("cancel")}
                onclick={() => setConfirmationDialogData(null)}
            />
        ]
    ));

    async function deleteOccurrence() {
        await CALENDAR_DELETE_OCCURRENCE.createRequest(null, { occurrenceId: occurrenceId })
            .send(setDisplaySpinner);

        setConfirmationDialogData(null);
        setSelectedEvent(null)
        callback();
    }
}

export default confirmOccurrenceDeletion;