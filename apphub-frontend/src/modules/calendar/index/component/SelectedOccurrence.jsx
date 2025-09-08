import { useState } from "react";
import ConfirmationDialog from "../../../../common/component/confirmation_dialog/ConfirmationDialog";
import useLoader from "../../../../common/hook/Loader";
import { CALENDAR_EDIT_EVENT_PAGE, CALENDAR_EDIT_OCCURRENCE_PAGE, CALENDAR_EDIT_OCCURRENCE_STATUS, CALENDAR_GET_OCCURRENCE } from "../../../../common/js/dao/endpoints/CalendarEndpoints";
import { hasValue, isBlank } from "../../../../common/js/Utils";
import LocalTime from "../../../../common/js/date/LocalTime";
import Button from "../../../../common/component/input/Button";
import Textarea from "../../../../common/component/input/Textarea";
import NotificationService from "../../../../common/js/notification/NotificationService";
import ErrorHandler from "../../../../common/js/dao/ErrorHandler";
import { ResponseStatus } from "../../../../common/js/dao/dao";
import { DONE, PENDING, SNOOZED } from "../../common/OccurrenceStatus";
import confirmEventDeletion from "../../common/delete_event/DeleteEvent";
import confirmOccurrenceDeletion from "../../common/delete_occurrence/DeleteOccurrence";

//TODO make it smaller
const SelectedOccurrence = ({
    occurrenceId,
    setDisplaySpinner,
    localizationHandler,
    setSelectedOccurrence,
    refresh,
    setConfirmationDialogData
}) => {
    const [occurrence, setOccurrence] = useState(null);

    useLoader({
        request: CALENDAR_GET_OCCURRENCE.createRequest(null, { occurrenceId: occurrenceId }),
        mapper: setOccurrence,
        listener: [occurrenceId],
        setDisplaySpinner: setDisplaySpinner,
        errorHandler: new ErrorHandler(
            (response) => response.status == ResponseStatus.NOT_FOUND,
            () => setSelectedOccurrence(null))
    });

    if (hasValue(occurrence)) {
        return <ConfirmationDialog
            id="calendar-selected-occurrence"
            title={occurrence.title}
            content={getContent()}
            choices={getChoices()}
        />
    }

    function getContent() {
        return (
            <div id="calendar-selected-occurrence-content">
                <div>{occurrence.date}</div>

                {hasValue(occurrence.time) && <div>{LocalTime.parse(occurrence.time).formatWithoutSeconds()}</div>}

                {!isBlank(occurrence.content) &&
                    <div>
                        <Textarea
                            id="calendar-selected-occurrence-content"
                            value={occurrence.content}
                            disabled={true}
                            rows={Math.max(3, occurrence.content.split("\n").length)}
                        />
                    </div>
                }

                {!isBlank(occurrence.note) &&
                    <div>
                        <Textarea
                            id="calendar-selected-occurrence-note"
                            value={occurrence.note}
                            disabled={true}
                            rows={Math.max(3, occurrence.note.split("\n").length)}
                        />
                    </div>
                }
            </div>
        );
    }

    function getChoices() {
        const choices = [];

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
    }
}

export default SelectedOccurrence;