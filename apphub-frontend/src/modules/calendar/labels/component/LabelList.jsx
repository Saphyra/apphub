import { useState } from "react";
import useLoader from "../../../../common/hook/Loader";
import { CALENDAR_GET_LABELS } from "../../../../common/js/dao/endpoints/CalendarEndpoints";
import InputField from "../../../../common/component/input/InputField";
import Stream from "../../../../common/js/collection/Stream";
import { cacheAndUpdate, cachedOrDefault, isBlank } from "../../../../common/js/Utils";
import Label from "./Label";

const CACHE_KEY_SEARCH_TEXT = "calendar.labels.searchText"

const LabelList = ({ selectedLabel, setSelectedLabel, localizationHandler, setDisplaySpinner, setConfirmationDialogData }) => {
    const [labels, setLabels] = useState([]);
    const [searchText, setSearchText] = useState(cachedOrDefault(CACHE_KEY_SEARCH_TEXT, ""));

    useLoader({
        request: CALENDAR_GET_LABELS.createRequest(),
        mapper: setLabels,
        setDisplaySpinner: setDisplaySpinner
    });

    return (
        <div id="calendar-labels-list">
            <InputField
                id="calendar-labels-list-search"
                placeholder={localizationHandler.get("search")}
                value={searchText}
                onchangeCallback={(v) => cacheAndUpdate(CACHE_KEY_SEARCH_TEXT, v, setSearchText)}
            />

            <div id="calendar-labels-list-content">
                {getLabelList()}
            </div>
        </div>
    );

    function getLabelList() {
        return new Stream(labels)
            .filter(label => isBlank(searchText) || label.label.toLowerCase().indexOf(searchText.toLowerCase()) > -1)
            .sorted((a, b) => a.label.localeCompare(b.label))
            .map(label => <Label
                key={label.labelId}
                localizationHandler={localizationHandler}
                setConfirmationDialogData={setConfirmationDialogData}
                setDisplaySpinner={setDisplaySpinner}
                labels={labels}
                setLabels={setLabels}
                label={label.label}
                labelId={label.labelId}
                selected={label.labelId === selectedLabel}
                setSelectedLabel={setSelectedLabel}
            />)
            .toList();
    }
}

export default LabelList;