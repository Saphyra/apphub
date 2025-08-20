import { useState } from "react";
import ConfirmationDialog from "../../../../common/component/confirmation_dialog/ConfirmationDialog";
import useLoader from "../../../../common/hook/Loader";
import { CALENDAR_EDIT_OCCURRENCE_PAGE, CALENDAR_EDIT_OCCURRENCE_STATUS, CALENDAR_GET_OCCURRENCE } from "../../../../common/js/dao/endpoints/CalendarEndpoints";
import { hasValue, isBlank } from "../../../../common/js/Utils";
import LocalTime from "../../../../common/js/date/LocalTime";
import Button from "../../../../common/component/input/Button";
import Textarea from "../../../../common/component/input/Textarea";
import { DONE, PENDING, SNOOZED } from "../../common/js/OccurrenceStatus";

const SelectedOccurrence = ({ occurrenceId, setDisplaySpinner, localizationHandler, setSelectedOccurrence, refresh }) => {
    const [occurrence, setOccurrence] = useState(null);

    useLoader({
        request: CALENDAR_GET_OCCURRENCE.createRequest(null, { occurrenceId: occurrenceId }),
        mapper: setOccurrence,
        listener: [occurrenceId],
        setDisplaySpinner: setDisplaySpinner
    })

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
            key="edit"
            onclick={() => window.location.href = CALENDAR_EDIT_OCCURRENCE_PAGE.assembleUrl({ occurrenceId: occurrence.occurrenceId })}
            label={localizationHandler.get("edit")}
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

/*
{
    "occurrenceId": "efe0c2ba-9697-41c0-8267-4531695e56c6",
    "eventId": "67dd987b-ed6c-4365-9efa-9a1aee246ac2",
    "date": "2025-08-20",
    "time": "09:32:00",
    "status": "PENDING",
    "title": "First event",
    "note": "",
    "remindMeBeforeDays": 1,
    "reminded": false
}
    */

export default SelectedOccurrence;