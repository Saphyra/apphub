import useLoader from "common/hook/Loader";
import LocalizationHandler from "common/js/LocalizationHandler";
import Stream from "common/js/collection/Stream";
import dayOfWeekLocalizationData from "common/js/date/day_of_week_localization.json";
import { useState } from "react";
import CalendarContentDay from "./CalendarContentDay";
import { DAYS_OF_WEEK } from "common/js/date/DayOfWeek";
import { hasValue } from "common/js/Utils";
import { CALENDAR_GET_OCCURRENCES } from "modules/feature/calendar/CalendarEndpoints";

const CalendarContent = ({
    view,
    activeLabel,
    showArchived,
    setDisplaySpinner,
    referenceDate,
    selectedDate,
    setSelectedDate,
    setSelectedOccurrence,
    refreshCounter
}) => {
    const dayOfWeekLocalizationHandler = new LocalizationHandler(dayOfWeekLocalizationData);

    const [occurrences, setOccurrences] = useState([]);

    useLoader(
        {
            request: CALENDAR_GET_OCCURRENCES.createRequest(null, null, getQueryParams()),
            mapper: setOccurrences,
            listener: [view, activeLabel, referenceDate, refreshCounter],
            setDisplaySpinner: setDisplaySpinner
        }
    );

    return (
        <div id="calendar-content">
            <div id="calendar-content-header">
                {getHeaders()}
            </div>

            <div id="calendar-content-body">
                {getBody()}
            </div>
        </div>
    );

    function getBody() {
        const startDate = view.startDate(referenceDate);
        const endDate = view.endDate(referenceDate);

        const days = [];
        for (let current = startDate; current.isBeforeInclusive(endDate); current = current.plusDays(1)) {
            days.push(current);
        }

        return new Stream(days)
            .sorted((a, b) => a.format().localeCompare(b.format()))
            .map(day =>
                <CalendarContentDay
                    key={day.format()}
                    day={day}
                    selectedDate={selectedDate}
                    occurrences={getOccurrences(day)}
                    setSelectedDate={setSelectedDate}
                    referenceDate={referenceDate}
                    setSelectedOccurrence={setSelectedOccurrence}
                />
            )
            .toList();

        function getOccurrences(day) {
            return new Stream(occurrences)
                .filter(occurrence => occurrence.date === day.toString())
                .filter(occurrence => showArchived || !occurrence.eventArchived)
                .toList();
        }
    }

    function getHeaders() {
        return new Stream(DAYS_OF_WEEK)
            .map(day =>
                <div
                    key={day}
                    className="calendar-content-day-header"
                >
                    {dayOfWeekLocalizationHandler.get(day)}
                </div>
            )
            .toList();
    }

    function getQueryParams() {
        const result = {};

        if (hasValue(activeLabel)) {
            result.labelId = activeLabel;
        }

        result.startDate = view.startDate(referenceDate);
        result.endDate = view.endDate(referenceDate);

        return result;
    }
}

export default CalendarContent;