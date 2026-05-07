const ExpiredEvent = ({ event, active, setSelectedEvent }) => {
    return (
        <div
            className={"calendar-expired-event button" + (event.archived ? " archived" : "") + (active ? " active" : "")}
            onClick={() => setSelectedEvent(event.eventId)}
        >
            <span className="calendar-expired-event-title">{event.title}</span>
        </div>
    );
}

export default ExpiredEvent;