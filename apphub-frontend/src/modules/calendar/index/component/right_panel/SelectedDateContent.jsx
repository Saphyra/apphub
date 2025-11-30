import { useState } from "react";
import Stream from "../../../../../common/js/collection/Stream";
import sortOccurrences from "../../../common/OccurrenceSorter";
import useLoader from "../../../../../common/hook/Loader";
import { CALENDAR_GET_OCCURRENCES } from "../../../../../common/js/dao/endpoints/CalendarEndpoints";
import Occurrence from "../../common/occurrence/Occurrence";
import { hasValue } from "../../../../../common/js/Utils";

const SelectedDateContent = ({ selectedDate, activeLabel, refreshCounter, setDisplaySpinner, setSelectedOccurrence }) => {
    const [occurrences, setOccurrences] = useState([]);

    useLoader(
        {
            request: CALENDAR_GET_OCCURRENCES.createRequest(null, null, getQueryParams()),
            mapper: setOccurrences,
            listener: [selectedDate, activeLabel, refreshCounter],
            setDisplaySpinner: setDisplaySpinner
        }
    );

    return (
        <div id="calendar-selected-date-content">
            {getContent()}
        </div>
    );

    function getContent() {
        return new Stream(occurrences)
            .sorted((a, b) => sortOccurrences(a, b))
            .map(occurrence => <Occurrence
                key={occurrence.occurrenceId}
                occurrence={occurrence}
                setSelectedOccurrence={setSelectedOccurrence}
            />)
            .toList();
    }

    function getQueryParams() {
        const result = {};

        if (hasValue(activeLabel)) {
            result.labelId = activeLabel;
        }

        result.startDate = selectedDate;
        result.endDate = selectedDate;

        return result;
    }
}

export default SelectedDateContent;