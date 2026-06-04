import { hasValue } from "common/js/Utils";
import "./occurrence.css"
import LocalTime from "common/js/date/LocalTime";

const Occurrence = ({ occurrence, setSelectedOccurrence }) => {
    return (
        <div
            className={
                "calendar-occurrence"
                + " calendar-occurrence-" + occurrence.status.toLowerCase()
                + (occurrence.eventArchived ? " calendar-occurrence-archived" : "")
            }
            onClick={(e) => {
                e.stopPropagation();
                setSelectedOccurrence({eventId: occurrence.eventId, occurrenceId: occurrence.occurrenceId});
            }}
        >
            {getTime()}
            <span> </span>
            <span className="calendar-occurrence-title">{occurrence.title}</span>
        </div>
    );

    function getTime() {
        if (hasValue(occurrence.time)) {
            return (
                <span className="calendar-occurrence-time">
                    {LocalTime.parse(occurrence.time).formatWithoutSeconds()}
                </span>
            );
        } else {
            return (
                <span className="calendar-occurrence-time invisible">
                    {LocalTime.now().formatWithoutSeconds()}
                </span>
            );
        }
    }
};

export default Occurrence;