import Button from "../../../../common/component/input/Button";
import { CALENDAR_EDIT_EVENT_PAGE, CALENDAR_EDIT_OCCURRENCE_PAGE, CALENDAR_EDIT_OCCURRENCE_STATUS, CALENDAR_OCCURRENCE_REMINDED } from "../../../../common/js/dao/endpoints/CalendarEndpoints";
import NotificationService from "../../../../common/js/notification/NotificationService";
import { hasValue } from "../../../../common/js/Utils";
import confirmEventDeletion from "../../common/delete_event/DeleteEvent";
import confirmOccurrenceDeletion from "../../common/delete_occurrence/DeleteOccurrence";
import { DONE, PENDING, SNOOZED } from "../../common/OccurrenceStatus";

function getChoices(args) {
    const {
        occurrence,
        localizationHandler,
        setOccurrence,
        setDisplaySpinner,
        setSelectedOccurrence,
        refresh,
        setConfirmationDialogData
    } = args;

    const choices = [];

    if (occurrence.status === PENDING && hasValue(occurrence.remindMeBeforeDays) && occurrence.remindMeBeforeDays > 0 && !occurrence.reminded) {
        choices.push(<Button
            key="reminder"
            onclick={() => setReminded()}
            label={localizationHandler.get("reminded")}
        />);
    }

    if (occurrence.status !== DONE) {
        choices.push(<Button
            key="done"
            onclick={() => editStatus(DONE)}
            label={localizationHandler.get("done")}
        />);
    }

    if (occurrence.status !== SNOOZED) {
        choices.push(<Button
            key="snooze"
            onclick={() => editStatus(SNOOZED)}
            label={localizationHandler.get("snooze")}
        />);
    }

    if (occurrence.status === SNOOZED || occurrence.status === DONE) {
        choices.push(<Button
            key="reset"
            onclick={() => editStatus(PENDING)}
            label={localizationHandler.get("reset")}
        />);
    }

    choices.push(<Button
        key="edit-occurrence"
        onclick={() => window.location.href = CALENDAR_EDIT_OCCURRENCE_PAGE.assembleUrl({ occurrenceId: occurrence.occurrenceId })}
        label={localizationHandler.get("edit-occurrence")}
    />);

    choices.push(<Button
        key="edit-event"
        onclick={() => window.location.href = CALENDAR_EDIT_EVENT_PAGE.assembleUrl({ eventId: occurrence.eventId })}
        label={localizationHandler.get("edit-event")}
    />);

    choices.push(<Button
        key="delete-occurrence"
        onclick={() => confirmOccurrenceDeletion(
            setConfirmationDialogData,
            occurrence.occurrenceId,
            occurrence.title,
            occurrence.date,
            setDisplaySpinner,
            setSelectedOccurrence,
            () => {
                NotificationService.showSuccess(localizationHandler.get("occurrence-deleted"));
                refresh();
            }
        )}
        label={localizationHandler.get("delete-occurrence")}
    />);

    choices.push(<Button
        key="delete-event"
        onclick={() => confirmEventDeletion(
            setConfirmationDialogData,
            occurrence.eventId,
            occurrence.title,
            setDisplaySpinner,
            setSelectedOccurrence,
            () => {
                NotificationService.showSuccess(localizationHandler.get("event-deleted"));
                refresh();
            }
        )}
        label={localizationHandler.get("delete-event")}
    />);

    choices.push(<Button
        key="cancel"
        onclick={() => setSelectedOccurrence(null)}
        label={localizationHandler.get("cancel")}
    />);

    return choices;

    async function editStatus(newStatus) {
        const response = await CALENDAR_EDIT_OCCURRENCE_STATUS.createRequest({ value: newStatus }, { occurrenceId: occurrence.occurrenceId })
            .send();

        setOccurrence(response);
        refresh();
    }

    async function setReminded() {
        const response = await CALENDAR_OCCURRENCE_REMINDED.createRequest(null, { occurrenceId: occurrence.occurrenceId })
            .send();

        setOccurrence(response);
        refresh();
    }
}

export default getChoices;