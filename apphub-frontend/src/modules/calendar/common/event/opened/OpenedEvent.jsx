import { useState } from "react";
import useLoader from "../../../../../common/hook/Loader";
import { CALENDAR_ARCHIVE_EVENT, CALENDAR_EDIT_EVENT_PAGE, CALENDAR_GET_EVENT, CALENDAR_GET_OCCURRENCES_OF_EVENT, CALENDAR_MERGE_EVENTS } from "../../../../../common/js/dao/endpoints/CalendarEndpoints";
import { hasValue, mapOrDefault } from "../../../../../common/js/Utils";
import Textarea from "../../../../../common/component/input/Textarea";
import LocalizationHandler from "../../../../../common/js/LocalizationHandler";
import repetitionTypeLocalizationData from "../../repetition_type/repetition_type_localization.json";
import { RepetitionType } from "../../repetition_type/RepetitionType";
import Button from "../../../../../common/component/input/Button";
import confirmEventDeletion from "../../delete_event/DeleteEvent";
import ConfirmationDialogData from "../../../../../common/component/confirmation_dialog/ConfirmationDialogData";
import Stream from "../../../../../common/js/collection/Stream";
import sortOccurrences from "../../occurrence/OccurrenceSorter";
import LocalTime from "../../../../../common/js/date/LocalTime";
import LocalDate from "../../../../../common/js/date/LocalDate";
import ErrorHandler from "../../../../../common/js/dao/ErrorHandler";
import localizationData from "./opened_event_localization.json";
import "./opened_event.css";

const OpenedEvent = ({
    eventId,
    backUrl,
    setSelectedEvent,
    setDisplaySpinner,
    setConfirmationDialogData,
    refresh,
    refreshCounter,
    selectedOccurrence,
    setSelectedOccurrence,
    renderAdditionalOperations = () => []
}) => {
    const localizationHandler = new LocalizationHandler(localizationData);
    const repetitionTypeLocalizationHandler = new LocalizationHandler(repetitionTypeLocalizationData);

    const [event, setEvent] = useState(null);
    const [occurrences, setOccurrences] = useState([]);

    useLoader({
        request: CALENDAR_GET_EVENT.createRequest(null, { eventId: eventId }),
        mapper: setEvent,
        setDisplaySpinner: setDisplaySpinner,
        condition: () => hasValue(eventId),
        listener: [eventId, refreshCounter],
        errorHandler: new ErrorHandler(
            response => response.status === 404,
            () => setSelectedEvent(null)
        )
    });

    useLoader({
        request: CALENDAR_GET_OCCURRENCES_OF_EVENT.createRequest(null, { eventId: eventId }),
        mapper: setOccurrences,
        setDisplaySpinner: setDisplaySpinner,
        listener: [eventId, refreshCounter]
    })

    if (hasValue(event)) {
        const textareaRows = Math.max(3, (event.content || "").split("\n").length);
        const additionalOperations = renderAdditionalOperations(event);

        return (
            <div id="calendar-opened-event">
                <div id="calendar-opened-event-title">{event.title}</div>

                <Textarea
                    id="calendar-opened-event-content"
                    value={event.content}
                    disabled={true}
                    rows={textareaRows}
                    placeholder={localizationHandler.get("content")}
                />

                <fieldset>
                    <legend>{localizationHandler.get("settings")}</legend>

                    <div id="calendar-opened-event-start-date">{localizationHandler.get("start-date", { date: event.startDate })}</div>

                    <div id="calendar-opened-event-end-date">{
                        localizationHandler.get(
                            "end-date",
                            mapOrDefault(
                                event.endDate,
                                { date: localizationHandler.get("not-set") },
                                v => { return { date: v } }
                            )
                        )
                    }
                    </div>

                    <div id="calendar-opened-event-time">{
                        localizationHandler.get(
                            "time",
                            mapOrDefault(
                                event.time,
                                { time: localizationHandler.get("not-set") },
                                v => { return { time: v } }
                            )
                        )}
                    </div>

                    <div id="calendar-opened-event-repetition-type">
                        <span>{localizationHandler.get("repetition-type")}</span>
                        <span>: </span>
                        <span>{repetitionTypeLocalizationHandler.get(event.repetitionType)}</span>
                    </div>

                    <div id="calendar-opened-event-repetition-data">{getRepetitionData()}</div>

                    <div id="calendar-opened-event-archived">
                        <span>{localizationHandler.get("archived")}</span>
                        <span>: </span>
                        <span>{localizationHandler.get(event.archived ? "true" : "false")}</span>
                    </div>

                    {event.repeatForDays > 1 &&
                        <div id="calendar-opened-event-repeat-for-days">
                            {localizationHandler.get("repeat-for-days", { days: event.repeatForDays })}
                        </div>
                    }

                    {event.remindMeBeforeDays > 0 &&
                        <div id="calendar-opened-event-remind-me-before-days">
                            {localizationHandler.get("remind-me-before-days", { days: event.remindMeBeforeDays })}
                        </div>
                    }
                </fieldset>

                <div id="calendar-opened-event-operations">
                    {additionalOperations}

                    <Button
                        id="calendar-opened-event-edit"
                        label={localizationHandler.get("edit")}
                        onclick={() => window.location.href = CALENDAR_EDIT_EVENT_PAGE.assembleUrl({ eventId: eventId }, { backUrl: backUrl })}
                    />

                    <Button
                        id="calendar-opened-event-delete"
                        label={localizationHandler.get("delete")}
                        onclick={() => confirmEventDeletion(
                            setConfirmationDialogData,
                            eventId,
                            event.title,
                            setDisplaySpinner,
                            setSelectedEvent,
                            refresh
                        )}
                    />

                    <Button
                        id="calendar-opened-event-merge-button"
                        label={localizationHandler.get("merge")}
                        onclick={confirmMerge}
                    />

                    <Button
                        id="calendar-opened-event-archive-button"
                        label={localizationHandler.get(event.archived ? "unarchive" : "archive")}
                        onclick={toggleArchive}
                    />
                </div>

                <fieldset>
                    <legend>{localizationHandler.get("occurrences")}</legend>

                    <div id="calendar-opened-event-occurrences">
                        {getOccurrences()}
                    </div>
                </fieldset>
            </div>

        );
    }

    function getRepetitionData() {
        return RepetitionType[event.repetitionType].display(event.repetitionData);
    }

    function confirmMerge() {
        setConfirmationDialogData(new ConfirmationDialogData(
            "calendar-opened-event-merge-confirmation-dialog",
            localizationHandler.get("confirm-merge-title"),
            localizationHandler.get("confirm-merge-content"),
            [
                <Button
                    key="confirm"
                    id="calendar-opened-event-merge-confirmation-dialog-confirm"
                    label={localizationHandler.get("merge")}
                    onclick={merge}
                />,
                <Button
                    key="cancel"
                    id="calendar-opened-event-merge-confirmation-dialog-cancel"
                    label={localizationHandler.get("cancel")}
                    onclick={() => setConfirmationDialogData(null)}
                />
            ]
        ));
    }

    async function merge() {
        await CALENDAR_MERGE_EVENTS.createRequest(null, { eventId: eventId })
            .send(setDisplaySpinner);

        setConfirmationDialogData(null);
        refresh();
    }

    async function toggleArchive() {
        const newArchived = !event.archived;

        await CALENDAR_ARCHIVE_EVENT.createRequest({ value: newArchived }, { eventId: eventId })
            .send(setDisplaySpinner);

        refresh();
    }

    function getOccurrences() {
        return new Stream(occurrences)
            .sorted(sortOccurrences)
            .map(occurrence => <Occurrence
                key={occurrence.occurrenceId}
                occurrence={occurrence}
                selectedOccurrence={selectedOccurrence}
                setSelectedOccurrence={setSelectedOccurrence}
            />
            )
            .toList();
    }
}

const Occurrence = ({ occurrence, selectedOccurrence, setSelectedOccurrence }) => {
    return (
        <div
            className={
                "calendar-opened-event-occurrence"
                + " calendar-opened-event-occurrence-" + occurrence.status.toLowerCase()
                + (occurrence.eventArchived ? " calendar-opened-event-occurrence-archived" : "")
                + (occurrence.occurrenceId === selectedOccurrence ? " active" : "")
            }
            onClick={() => setSelectedOccurrence(occurrence.occurrenceId)}
        >
            <span className={"calendar-opened-event-occurrence-time" + (hasValue(occurrence.time) ? "" : " calendar-opened-event-occurrence-time-hidden")}>
                {(hasValue(occurrence.time) ? LocalTime.parse(occurrence.time) : LocalTime.of()).formatWithoutSeconds()}
            </span>
            <span> </span>
            <span className="calendar-opened-event-occurrence-date">
                {LocalDate.parse(occurrence.date).format()}
            </span>
        </div>
    );
}

export default OpenedEvent;