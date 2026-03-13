import Stream from "../../../../common/js/collection/Stream";
import MatchingEvent from "./MatchingEvent";

const EventList = ({ events, localizationHandler, selectedEvent, setSelectedEvent }) => {
    return (
        <div id="calendar-search-event-list">
            {getContent()}
        </div>
    );

    function getContent() {
        return new Stream(events)
            .sorted((a, b) => a.title.localeCompare(b.title))
            .map(event => <MatchingEvent
                key={event.eventId}
                event={event}
                active={selectedEvent === event.eventId}
                setSelectedEvent={setSelectedEvent}
            />)
            .toList();
    }
}

export default EventList;