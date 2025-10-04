import Stream from "../../../../../common/js/collection/Stream";
import LocalDate from "../../../../../common/js/date/LocalDate";
import sortOccurrences from "../../../common/OccurrenceSorter";
import Occurrence from "../../common/occurrence/Occurrence";

const CalendarContentDay = ({ day, occurrences, selectedDate, setSelectedDate, referenceDate, setSelectedOccurrence }) => {
    return (
        <div
            className={
                "calendar-content-day"
                + (day.equals(selectedDate) ? " active" : "")
                + (day.getMonth() !== referenceDate.getMonth() ? " filler" : "")
                + (day.equals(LocalDate.now()) ? " today" : "")
            }
            onClick={() => setSelectedDate(day)}
        >
            <div className="calendar-content-day-title">
                {day.getDay()}
            </div>

            <div className="calendar-content-day-occurrences">
                {getContent()}
            </div>
        </div>
    );

    function getContent() {
        return new Stream(occurrences)
            .filter(occurrence => occurrence.date === day.toString())
            .sorted((a, b) => sortOccurrences(a, b))
            .map(occurrence => <Occurrence
                key={occurrence.occurrenceId}
                occurrence={occurrence}
                setSelectedOccurrence={setSelectedOccurrence}
            />)
            .toList();
    }
}

export default CalendarContentDay;