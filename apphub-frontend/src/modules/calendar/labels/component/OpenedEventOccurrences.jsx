import { useState } from "react";
import useLoader from "../../../../common/hook/Loader";
import { CALENDAR_GET_OCCURRENCES_OF_EVENT } from "../../../../common/js/dao/endpoints/CalendarEndpoints";
import Stream from "../../../../common/js/collection/Stream";
import { hasValue } from "../../../../common/js/Utils";
import LocalTime from "../../../../common/js/date/LocalTime";
import LocalDate from "../../../../common/js/date/LocalDate";
import sortOccurrences from "../../common/OccurrenceSorter";

const OpenedEventOccurrences = ({ eventId, setDisplaySpinner, selectedOccurrence, setSelectedOccurrence, refreshCounter }) => {
    const [occurrences, setOccurrences] = useState([]);

    useLoader({
        request: CALENDAR_GET_OCCURRENCES_OF_EVENT.createRequest(null, { eventId: eventId }),
        mapper: setOccurrences,
        setDisplaySpinner: setDisplaySpinner,
        listener: [eventId, refreshCounter]
    })

    return (
        <div id="calendar-labels-event-occurrences">
            {getContent()}
        </div>
    );

    function getContent() {
        return new Stream(occurrences)
            .sorted(sortOccurrences)
            .map(occurrence => <Occurrence
                key={occurrence.occurrenceId}
                occurrence={occurrence}
                selectedOccurrence={selectedOccurrence}
                setSelectedOccurrence={setSelectedOccurrence}
            />
            )
            .toList();
    }
}

const Occurrence = ({ occurrence, selectedOccurrence, setSelectedOccurrence }) => {
    return (
        <div
            className={
                "calendar-occurrence calendar-labels-event-occurrence calendar-occurrence-"
                + occurrence.status.toLowerCase()
                + (occurrence.occurrenceId === selectedOccurrence ? " active" : "")
            }
            onClick={() => setSelectedOccurrence(occurrence.occurrenceId)}
        >
            <span className={"calendar-labels-event-occurrence-time" + (hasValue(occurrence.time) ? "" : " occurrence-time-hidden")}>
                {(hasValue(occurrence.time) ? LocalTime.parse(occurrence.time) : LocalTime.of()).formatWithoutSeconds()}
            </span>
            <span> </span>
            <span className="calendar-labels-event-occurrence-date">
                {LocalDate.parse(occurrence.date).format()}
            </span>
        </div>
    )
}

export default OpenedEventOccurrences;