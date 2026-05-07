const MatchingEvent = ({ event, active, setSelectedEvent }) => {
    return (
        <div
            className={"calendar-search-event button" + (event.archived ? " archived" : "") + (active ? " active" : "")}
            onClick={() => setSelectedEvent(event.eventId)}
        >
            <span className="calendar-search-event-title">{event.title}</span>
        </div>
    );
}

export default MatchingEvent;