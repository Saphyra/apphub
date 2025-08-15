import { useState } from "react";
import { CALENDAR_CREATE_EVENT_PAGE, CALENDAR_GET_OCCURRENCES } from "../../../../../common/js/dao/endpoints/CalendarEndpoints";
import useLoader from "../../../../../common/hook/Loader";
import { hasValue, throwException } from "../../../../../common/js/Utils";
import localizationData from "../../localization/selected_date_localization.json";
import LocalizationHandler from "../../../../../common/js/LocalizationHandler";

const SelectedDate = ({ selectedDate, activeLabel, setDisplaySpinner }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

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

            <div id="calendar-selected-date-content">{getContent()}</div>

            <div
                className="button"
                onClick={() => window.location.href = CALENDAR_CREATE_EVENT_PAGE.assembleUrl(null, { startDate: selectedDate.format() })}
            >
                {localizationHandler.get("create-new")}
            </div>
        </div>
    );

    function getContent() {
    }

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