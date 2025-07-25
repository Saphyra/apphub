import { useState } from "react";
import Stream from "../../../../../common/js/collection/Stream";
import LocalizationHandler from "../../../../../common/js/LocalizationHandler";
import { DAYS_OF_WEEK } from "../../../../../common/js/date/DayOfWeek";
import dayOfWeekLocalizationData from "../../localization/day_of_week_localization.json";
import useLoader from "../../../../../common/hook/Loader";
import { CALENDAR_GET_OCCURRENCES } from "../../../../../common/js/dao/endpoints/CalendarEndpoints";
import { hasValue } from "../../../../../common/js/Utils";
import CalendarContentDay from "./CalendarContentDay";
import LocalDate from "../../../../../common/js/date/LocalDate";

const CalendarContent = ({ view, activeLabel, setDisplaySpinner, referenceDate, selectedDate, setSelectedDate }) => {
    const dayOfWeekLocalizationHandler = new LocalizationHandler(dayOfWeekLocalizationData);

    const [occurrences, setOccurrences] = useState([]);

    useLoader(
        {
            request: CALENDAR_GET_OCCURRENCES.createRequest(null, null, getQueryParams()),
            mapper: setOccurrences,
            listener: [view, activeLabel, referenceDate],
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
                />
            )
            .toList();

        function getOccurrences(day) {
            return new Stream(occurrences)
                .filter(occurrence => occurrence.date === day.toString())
                .toList();
        }
    }

    function getHeaders() {
        return new Stream(DAYS_OF_WEEK)
            .map(day =>
                <div
                    key={day}
                    className="day-header"
                >
                    {dayOfWeekLocalizationHandler.get(day)}
                </div>
            )
            .toList();
    }

    function getQueryParams() {
        const result = {};

        if (hasValue(activeLabel)) {
            result.label = activeLabel;
        }

        result.startDate = view.startDate(referenceDate);
        result.endDate = view.endDate(referenceDate);

        return result;
    }
}

export default CalendarContent;