import Constants from "common/js/Constants";

const Event = ({ event, active, setSelectedEvent }) => {
    return (
        <div
            className={"calendar-labels-event button" + (event.archived ? " archived" : "") + (active ? " active" : "")}
            onClick={() => setSelectedEvent(event.eventId)}
        >
            <span className="calendar-labels-event-title">{event.title + (event.shared ? Constants.ICON_SHARED : "")}</span>
        </div>
    );
}

export default Event;