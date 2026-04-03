import Stream from "../../../../common/js/collection/Stream";
import MatchingEvent from "./MatchingEvent";
import sortEvents from "../../common/event/sortEvents";

const EventList = ({ events, localizationHandler, selectedEvent, setSelectedEvent }) => {
    return (
        <div id="calendar-search-event-list">
            {getContent()}
        </div>
    );

    function getContent() {
        return new Stream(events)
            .sorted(sortEvents)
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