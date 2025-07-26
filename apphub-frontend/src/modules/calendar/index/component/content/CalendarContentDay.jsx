import LocalDate from "../../../../../common/js/date/LocalDate";

const CalendarContentDay = ({ day, occurrences, selectedDate, setSelectedDate, referenceDate}) => {
    return (
        <div
            className={
                "day"
                + (day.equals(selectedDate) ? " active" : "")
                + (day.getMonth() !== referenceDate.getMonth() ? " filler" : "")
                + (day.equals(LocalDate.now()) ? " today" : "")
            }
            onClick={() => setSelectedDate(day)}
        >
            {day.getDay()}
        </div>
    );
}

export default CalendarContentDay;