import LocalTime from "../../../../../common/js/date/LocalTime";
import { hasValue } from "../../../../../common/js/Utils";
import "./occurrence.css"

const Occurrence = ({ occurrence, setSelectedOccurrence }) => {
    return (
        <div
            className={"calendar-occurrence calendar-occurrence-" + occurrence.status.toLowerCase()}
            onClick={(e) => {
                e.stopPropagation();
                setSelectedOccurrence(occurrence.occurrenceId)
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