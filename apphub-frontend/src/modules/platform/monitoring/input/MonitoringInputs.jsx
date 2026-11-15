import { useState } from "react";
import TimeFrame from "./time_frame/TimeFrame";
import TimeFrameSelector from "./time_frame/TimeFrameSelector";
import FeatureSelector from "./feature/FeatureSelector";
import { hasValue, isBlank, isNullOrEmpty, nullIfEmpty } from "../../../../common/js/Utils";
import FunctionalitySelector from "./functionality/FunctionalitySelector";
import ServiceSelector from "./service/ServiceSelector";
import Button from "../../../../common/component/input/Button";
import PreLabeledInputField from "common/component/input/PreLabeledInputField";
import InputField from "common/component/input/InputField";
import BoundarySelector from "./boundary/BoundarySelector";
import Optional from "common/js/collection/Optional";

const MonitoringInputs = ({ localizationHandler, setDisplaySpinner, queryData, setQueryData, filterText, setFilterText }) => {
    const [timeFrame, setTimeFrame] = useState(TimeFrame.SECOND);
    const [feature, setFeature] = useState("");
    const [functionality, setFunctionality] = useState("");
    const [service, setService] = useState("");
    const [startTime, setStartTime] = useState("");
    const [endTime, setEndTime] = useState("");

    return (
        <div id="monitoring-inputs">
            <TimeFrameSelector
                localizationHandler={localizationHandler}
                timeFrame={timeFrame}
                setTimeFrame={setTimeFrame}
            />

            <FeatureSelector
                localizationHandler={localizationHandler}
                setDisplaySpinner={setDisplaySpinner}
                feature={feature}
                setFeature={setFeature}
            />

            {!isBlank(feature) &&
                <FunctionalitySelector
                    localizationHandler={localizationHandler}
                    setDisplaySpinner={setDisplaySpinner}
                    functionality={functionality}
                    setFunctionality={setFunctionality}
                    feature={feature}
                />
            }

            {!isBlank(feature) &&
                <ServiceSelector
                    localizationHandler={localizationHandler}
                    setDisplaySpinner={setDisplaySpinner}
                    service={service}
                    setService={setService}
                    feature={feature}
                    functionality={functionality}
                />
            }

            <BoundarySelector
                boundary={startTime}
                setBoundary={setStartTime}
                label={localizationHandler.get("start_time")}
                validation={(value) => validateAfter(value, endTime, localizationHandler.get("start-time-after-end-time"))}
            />
            <BoundarySelector
                boundary={endTime}
                setBoundary={setEndTime}
                label={localizationHandler.get("end_time")}
                validation={(value) => validateAfter(startTime, value, localizationHandler.get("end-time-after-start-time"))}
            />

            <Button
                id="monitoring-load-button"
                label={localizationHandler.get("load")}
                onclick={assembleQueryData}
                disabled={isBlank(feature)}
            />

            {hasValue(queryData) &&
                <PreLabeledInputField
                    label={localizationHandler.get("filter")}
                    input={<InputField
                        id="monitoring-filter"
                        value={filterText}
                        onchangeCallback={setFilterText}
                        placeholder={localizationHandler.get("filter")}
                    />
                    }
                />
            }
        </div>
    );

    function assembleQueryData() {
        const data = {
            timeFrame: timeFrame,
            feature: feature,
            functionality: nullIfEmpty(functionality),
            service: nullIfEmpty(service),
            startTime: nullIfEmpty(startTime),
            endTime: nullIfEmpty(endTime)
        }
        setQueryData(data);
    }

    /*
    Validates that the second boundary is after the first boundary.
    Returns an Optional containing an error message if validation fails, or an empty Optional if it passes.
    If either value is empty or null, validation passes.
    */
    function validateAfter(first, second, errorMessage) {
        if (isNullOrEmpty(first) || isNullOrEmpty(second)) {
            return new Optional(null);
        }

        if (second >= first) {
            return new Optional(null);
        }

        return new Optional(errorMessage);
    }
}

export default MonitoringInputs;