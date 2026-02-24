import { useState } from "react";
import InputField from "../../../../common/component/input/InputField";
import ExpiredEvent from "./ExpiredEvent";
import Stream from "../../../../common/js/collection/Stream";

const ExpiredEventList = ({ events, localizationHandler, selectedEvent, setSelectedEvent }) => {
    const [search, setSearch] = useState("");

    return (
        <div id="calendar-expired-event-list">
            <InputField
                id="calendar-expired-event-search"
                placeholder={localizationHandler.get("search")}
                value={search}
                onchangeCallback={setSearch}
            />

            {getContent()}
        </div>
    );

    function getContent() {
        return new Stream(events)
            .sorted((a, b) => a.title.localeCompare(b.title))
            .filter(event => event.title.toLowerCase().includes(search.toLowerCase()))
            .map(event => <ExpiredEvent
                key={event.eventId}
                event={event}
                active={selectedEvent === event.eventId}
                setSelectedEvent={setSelectedEvent}
            />)
            .toList();
    }
}

export default ExpiredEventList;