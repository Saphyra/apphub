import { useState } from "react";
import useLoader from "../../../../common/hook/Loader";
import { CALENDAR_GET_EVENTS, CALENDAR_GET_LABEL } from "../../../../common/js/dao/endpoints/CalendarEndpoints";
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
    });
    useLoader({
        request: CALENDAR_GET_LABEL.createRequest(null, { labelId: selectedLabel }),
        mapper: setLabel,
        setDisplaySpinner: setDisplaySpinner,
        listener: [selectedLabel],
    });

    return (
        <div id="calendar-labels-events">
            {hasValue(label) &&
                <div id="calendar-labels-events-title">
                    {localizationHandler.get("events-of-label", { label: label.label })}
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