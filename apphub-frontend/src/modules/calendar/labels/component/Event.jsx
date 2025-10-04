const Event = ({ event, setDisplaySpinner, active, setSelectedEvent }) => {
    return (
        <div
            className={"calendar-labels-event button" + (active ? " active" : "")}
            onClick={() => setSelectedEvent(event.eventId)}
        >
            <span className="calendar-labels-event-title">{event.title}</span>
        </div>
    );
}

export default Event;