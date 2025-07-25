import { useState } from "react";
import { CALENDAR_GET_OCCURRENCES } from "../../../../../common/js/dao/endpoints/CalendarEndpoints";
import useLoader from "../../../../../common/hook/Loader";
import { hasValue } from "../../../../../common/js/Utils";

const SelectedDate = ({ selectedDate, activeLabel , setDisplaySpinner}) => {
    const [occurrences, setOccurrences] = useState([]);

    useLoader(
        {
            request: CALENDAR_GET_OCCURRENCES.createRequest(null, null, getQueryParams()),
            mapper: setOccurrences,
            listener: [selectedDate, activeLabel],
            setDisplaySpinner: setDisplaySpinner
        }
    );

    return (
        <div id="calendar-selected-date">
            <div id="calendar-selected-date-title">{selectedDate.format()}</div>
        </div>
    );

    function getQueryParams() {
        const result = {};

        if (hasValue(activeLabel)) {
            result.label = activeLabel;
        }

        result.startDate = selectedDate;
        result.endDate = selectedDate;

        return result;
    }
}

export default SelectedDate;