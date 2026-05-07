const Event = ({ event, active, setSelectedEvent }) => {
    return (
        <div
            className={"calendar-labels-event button" + (event.archived ? " archived" : "") + (active ? " active" : "")}
            onClick={() => setSelectedEvent(event.eventId)}
        >
            <span className="calendar-labels-event-title">{event.title}</span>
        </div>
    );
}

export default Event;