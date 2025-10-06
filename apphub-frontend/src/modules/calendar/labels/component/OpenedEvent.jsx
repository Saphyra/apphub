import { useEffect, useState } from "react";
import useLoader from "../../../../common/hook/Loader";
import { CALENDAR_EDIT_EVENT_PAGE, CALENDAR_GET_EVENT, CALENDAR_LABELS_PAGE } from "../../../../common/js/dao/endpoints/CalendarEndpoints";
import { hasValue, mapOrDefault } from "../../../../common/js/Utils";
import Textarea from "../../../../common/component/input/Textarea";
import repetitionTypeLocalizationData from "../../common/repetition_type/repetition_type_localization.json";
import LocalizationHandler from "../../../../common/js/LocalizationHandler";
import Button from "../../../../common/component/input/Button";
import OpenedEventOccurrences from "./OpenedEventOccurrences";
import { RepetitionType } from "../../common/repetition_type/RepetitionType";
import confirmEventDeletion from "../../common/delete_event/DeleteEvent";

const OpenedEvent = ({
    eventId,
    localizationHandler,
    setDisplaySpinner,
    selectedOccurrence,
    setSelectedOccurrence,
    refreshCounter,
    setConfirmationDialogData,
    setSelectedEvent,
    refresh
}) => {
    const repetitionTypeLocalizationHandler = new LocalizationHandler(repetitionTypeLocalizationData);

    const [event, setEvent] = useState(null);

    useLoader({
        request: CALENDAR_GET_EVENT.createRequest(null, { eventId: eventId }),
        mapper: setEvent,
        setDisplaySpinner: setDisplaySpinner,
        condition: () => hasValue(eventId),
        listener: [eventId]
    });

    if (hasValue(event)) {
        const textareaRows = Math.max(3, event.content.split("\n").length);

        return (
            <div id="calendar-labels-event">
                <div id="calendar-labels-event-title">{event.title}</div>

                <Textarea
                    id="calendar-labels-event-content"
                    value={event.content}
                    disabled={true}
                    rows={textareaRows}
                    placeholder={localizationHandler.get("content")}
                />

                <fieldset>
                    <legend>{localizationHandler.get("settings")}</legend>

                    <div id="calendar-labels-event-start-date">{localizationHandler.get("start-date", { date: event.startDate })}</div>

                    <div id="calendar-labels-event-end-date">{
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

                    <div id="calendar-labels-event-time">{
                        localizationHandler.get(
                            "time",
                            mapOrDefault(
                                event.time,
                                { time: localizationHandler.get("not-set") },
                                v => { return { time: v } }
                            )
                        )}
                    </div>

                    <div id="calendar-labels-event-repetition-type">
                        <span>{localizationHandler.get("repetition-type")}</span>
                        <span>: </span>
                        <span>{repetitionTypeLocalizationHandler.get(event.repetitionType)}</span>
                    </div>

                    <div id="calendar-labels-event-repetition-data">{getRepetitionData()}</div>

                    {event.repeatForDays > 1 && <div id="calendar-labels-event-repeat-for-days">{localizationHandler.get("repeat-for-days", { days: event.repeatForDays })}</div>}

                    {event.remindMeBeforeDays > 0 && <div id="calendar-labels-event-remind-me-before-days">{localizationHandler.get("remind-me-before-days", { days: event.remindMeBeforeDays })}</div>}
                </fieldset>

                <div id="calendar-labels-event-operations">
                    <Button
                        id="calendar-labels-event-edit"
                        label={localizationHandler.get("edit")}
                        onclick={() => window.location.href = CALENDAR_EDIT_EVENT_PAGE.assembleUrl({ eventId: eventId }, { backUrl: CALENDAR_LABELS_PAGE })}
                    />

                    <Button
                        id="calendar-labels-event-delete"
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

                    <OpenedEventOccurrences
                        eventId={eventId}
                        setDisplaySpinner={setDisplaySpinner}
                        selectedOccurrence={selectedOccurrence}
                        setSelectedOccurrence={setSelectedOccurrence}
                        refreshCounter={refreshCounter}
                    />
                </fieldset>
            </div>

        );
    }

    function getRepetitionData() {
        return RepetitionType[event.repetitionType].display(event.repetitionData);
    }
}

export default OpenedEvent;