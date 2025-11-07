import { useState } from "react";
import useLoader from "../../../../common/hook/Loader";
import { CALENDAR_GET_EVENTS, CALENDAR_GET_LABEL, CALENDAR_GET_LABELLESS_EVENTS } from "../../../../common/js/dao/endpoints/CalendarEndpoints";
import { hasValue } from "../../../../common/js/Utils";
import Stream from "../../../../common/js/collection/Stream";
import Event from "./Event";

const Events = ({ selectedLabel, localizationHandler, setDisplaySpinner, selectedEvent, setSelectedEvent, refreshCounter }) => {
    const [events, setEvents] = useState([]);
    const [label, setLabel] = useState(null);

    useLoader({
        request: CALENDAR_GET_EVENTS.createRequest(null, null, { labelId: selectedLabel }),
        mapper: setEvents,
        setDisplaySpinner: setDisplaySpinner,
        listener: [selectedLabel, refreshCounter],
        condition: () => hasValue(selectedLabel)
    });
    useLoader({
        request: CALENDAR_GET_LABEL.createRequest(null, { labelId: selectedLabel }),
        mapper: setLabel,
        setDisplaySpinner: setDisplaySpinner,
        listener: [selectedLabel],
        condition: () => hasValue(selectedLabel)
    });
    useLoader({
        request: CALENDAR_GET_LABELLESS_EVENTS.createRequest(),
        mapper: setEvents,
        setDisplaySpinner: setDisplaySpinner,
        listener: [selectedLabel, refreshCounter],
        condition: () => !hasValue(selectedLabel)
    });

    return (
        <div id="calendar-labels-events">
            {hasValue(label) &&
                <div id="calendar-labels-events-title">
                    {localizationHandler.get("events-of-label", { label: label.label })}
                </div>
            }

            {!hasValue(label) &&
                <div id="calendar-labels-events-title">
                    {localizationHandler.get("events-without-label")}
                </div>
            }

            {getContent()}
        </div>
    );

    function getContent() {
        return new Stream(events)
            .sorted((a, b) => a.title.localeCompare(b.title))
            .map(event => <Event
                key={event.eventId}
                event={event}
                setDisplaySpinner={setDisplaySpinner}
                active={selectedEvent === event.eventId}
                setSelectedEvent={setSelectedEvent}
            />)
            .toList();
    }
}

export default Events;