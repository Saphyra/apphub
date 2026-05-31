import LocalizationHandler from "common/js/LocalizationHandler";
import localizationData from "./opened_occurrence_localization.json";
import "./opened_occurrence.css";
import { useState } from "react";
import useLoader from "common/hook/Loader";
import { hasValue } from "common/js/Utils";
import ErrorHandler from "common/js/dao/ErrorHandler";
import LocalTime from "common/js/date/LocalTime";
import Textarea from "common/component/input/Textarea";
import { DONE, PENDING, SNOOZED } from "./OccurrenceStatus";
import Button from "common/component/input/Button";
import confirmOccurrenceDeletion from "../delete_occurrence/DeleteOccurrence";
import NotificationService from "common/js/notification/NotificationService";
import { CALENDAR_EDIT_OCCURRENCE_PAGE, CALENDAR_EDIT_OCCURRENCE_STATUS, CALENDAR_GET_OCCURRENCE } from "../../CalendarEndpoints";

const OpenedOccurrence = ({ eventId, occurrenceId, setConfirmationDialogData, setDisplaySpinner, setSelectedOccurrence, refreshCounter, refresh, backUrl }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [occurrence, setOccurrence] = useState(null);

    useLoader(
        {
            request: CALENDAR_GET_OCCURRENCE.createRequest(null, { eventId: eventId, occurrenceId: occurrenceId }),
            mapper: setOccurrence,
            condition: () => hasValue(occurrenceId),
            listener: [occurrenceId, refreshCounter],
            errorHandler: new ErrorHandler(
                (response) => response.status == 404,
                () => setSelectedOccurrence(null))
        }
    );

    if (hasValue(occurrence)) {
        return (
            <div id="calendar-opened-occurrence">
                <div id="calendar-opened-occurrence-title">{occurrence.date}</div>

                <div id="calendar-opened-occurrence-content">
                    {hasValue(occurrence.time) &&
                        <div id="calendar-opened-occurrence-time">
                            <span>{localizationHandler.get("at")}</span>
                            <span>: </span>
                            <span>{LocalTime.parse(occurrence.time).formatWithoutSeconds()}</span>
                        </div>
                    }

                    <Textarea
                        id="calendar-opened-occurrence-content"
                        value={occurrence.note}
                        disabled={true}
                        rows={Math.max(3, occurrence.note.split("\n").length)}
                        placeholder={localizationHandler.get("note")}
                    />

                    {occurrence.remindMeBeforeDays > 0 &&
                        <div id="calendar-opened-occurrence-reminder">
                            {localizationHandler.get("reminder", { days: occurrence.remindMeBeforeDays })}
                        </div>
                    }

                    {occurrence.remindMeBeforeDays > 0 &&
                        <div id="calendar-opened-occurrence-reminded">
                            {localizationHandler.get("reminder-confirmed", { value: localizationHandler.get(occurrence.reminded) })}
                        </div>
                    }
                </div>


                {getOperations()}
            </div>
        );
    }

    function getOperations() {
        return (
            <div id="calendar-opened-occurrence-operations">
                {occurrence.status !== DONE &&
                    <Button
                        onclick={() => editStatus(DONE)}
                        label={localizationHandler.get("done")}
                    />
                }

                {occurrence.status !== SNOOZED &&
                    <Button
                        onclick={() => editStatus(SNOOZED)}
                        label={localizationHandler.get("snooze")}
                    />
                }

                {(occurrence.status === SNOOZED || occurrence.status === DONE) &&
                    <Button
                        onclick={() => editStatus(PENDING)}
                        label={localizationHandler.get("reset")}
                    />
                }

                <Button
                    onclick={() => window.location.href = CALENDAR_EDIT_OCCURRENCE_PAGE.assembleUrl({ eventId: occurrence.eventId, occurrenceId: occurrence.occurrenceId }, { backUrl: backUrl })}
                    label={localizationHandler.get("edit-occurrence")}
                />

                <Button
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
                />
            </div>
        );

        async function editStatus(newStatus) {
            const response = await CALENDAR_EDIT_OCCURRENCE_STATUS.createRequest({ value: newStatus }, { eventId: occurrence.eventId, occurrenceId: occurrence.occurrenceId })
                .send();

            setOccurrence(response);
            refresh();
        }
    }
}

export default OpenedOccurrence;