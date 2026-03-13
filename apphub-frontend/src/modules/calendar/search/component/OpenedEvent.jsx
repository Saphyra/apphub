import { useState } from "react";
import LocalizationHandler from "../../../../common/js/LocalizationHandler";
import repetitionTypeLocalizationData from "../../common/repetition_type/repetition_type_localization.json";
import useLoader from "../../../../common/hook/Loader";
import { hasValue, mapOrDefault } from "../../../../common/js/Utils";
import Textarea from "../../../../common/component/input/Textarea";
import Button from "../../../../common/component/input/Button";
import confirmEventDeletion from "../../common/delete_event/DeleteEvent";
import { RepetitionType } from "../../common/repetition_type/RepetitionType";
import Stream from "../../../../common/js/collection/Stream";
import sortOccurrences from "../../common/occurrence/OccurrenceSorter";
import LocalTime from "../../../../common/js/date/LocalTime";
import LocalDate from "../../../../common/js/date/LocalDate";
import { CALENDAR_EDIT_EVENT_PAGE, CALENDAR_GET_EVENT, CALENDAR_GET_OCCURRENCES_OF_EVENT, CALENDAR_SEARCH_PAGE } from "../../../../common/js/dao/endpoints/CalendarEndpoints";

const OpenedEvent = ({
    eventId,
    setSelectedEvent,
    setDisplaySpinner,
    localizationHandler,
    setConfirmationDialogData,
    refresh,
    refreshCounter,
    selectedOccurrence,
    setSelectedOccurrence
}) => {
    const repetitionTypeLocalizationHandler = new LocalizationHandler(repetitionTypeLocalizationData);

    const [event, setEvent] = useState(null);
    const [occurrences, setOccurrences] = useState([]);

    useLoader({
        request: CALENDAR_GET_EVENT.createRequest(null, { eventId: eventId }),
        mapper: setEvent,
        setDisplaySpinner: setDisplaySpinner,
        condition: () => hasValue(eventId),
        listener: [eventId]
    });

    useLoader({
        request: CALENDAR_GET_OCCURRENCES_OF_EVENT.createRequest(null, { eventId: eventId }),
        mapper: setOccurrences,
        setDisplaySpinner: setDisplaySpinner,
        listener: [eventId, refreshCounter]
    })

    if (hasValue(event)) {
        const textareaRows = Math.max(3, event.content.split("\n").length);

        return (
            <div id="calendar-search-event">
                <div id="calendar-search-event-title">{event.title}</div>

                <Textarea
                    id="calendar-search-event-content"
                    value={event.content}
                    disabled={true}
                    rows={textareaRows}
                    placeholder={localizationHandler.get("content")}
                />

                <fieldset>
                    <legend>{localizationHandler.get("settings")}</legend>

                    <div id="calendar-search-event-start-date">{localizationHandler.get("start-date", { date: event.startDate })}</div>

                    <div id="calendar-search-event-end-date">{
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

                    <div id="calendar-search-event-time">{
                        localizationHandler.get(
                            "time",
                            mapOrDefault(
                                event.time,
                                { time: localizationHandler.get("not-set") },
                                v => { return { time: v } }
                            )
                        )}
                    </div>

                    <div id="calendar-search-event-repetition-type">
                        <span>{localizationHandler.get("repetition-type")}</span>
                        <span>: </span>
                        <span>{repetitionTypeLocalizationHandler.get(event.repetitionType)}</span>
                    </div>

                    <div id="calendar-search-event-repetition-data">{getRepetitionData()}</div>

                    {event.repeatForDays > 1 && <div id="calendar-search-event-repeat-for-days">{localizationHandler.get("repeat-for-days", { days: event.repeatForDays })}</div>}

                    {event.remindMeBeforeDays > 0 && <div id="calendar-search-event-remind-me-before-days">{localizationHandler.get("remind-me-before-days", { days: event.remindMeBeforeDays })}</div>}
                </fieldset>

                <div id="calendar-search-event-operations">
                    <Button
                        id="calendar-search-event-edit"
                        label={localizationHandler.get("edit")}
                        onclick={() => window.location.href = CALENDAR_EDIT_EVENT_PAGE.assembleUrl({ eventId: eventId }, { backUrl: CALENDAR_SEARCH_PAGE })}
                    />

                    <Button
                        id="calendar-search-event-delete"
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
                </div>

                <fieldset>
                    <legend>{localizationHandler.get("occurrences")}</legend>

                    {getOccurrences()}
                </fieldset>
            </div >

        );
    }

    function getRepetitionData() {
        return RepetitionType[event.repetitionType].display(event.repetitionData);
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
                "calendar-occurrence calendar-search-event-occurrence calendar-occurrence-"
                + occurrence.status.toLowerCase()
                + (occurrence.occurrenceId === selectedOccurrence ? " active" : "")
            }
            onClick={() => setSelectedOccurrence(occurrence.occurrenceId)}
        >
            <span className={"calendar-search-event-occurrence-time" + (hasValue(occurrence.time) ? "" : " occurrence-time-hidden")}>
                {(hasValue(occurrence.time) ? LocalTime.parse(occurrence.time) : LocalTime.of()).formatWithoutSeconds()}
            </span>
            <span> </span>
            <span className="calendar-search-event-occurrence-date">
                {LocalDate.parse(occurrence.date).format()}
            </span>
        </div>
    )
}

export default OpenedEvent;