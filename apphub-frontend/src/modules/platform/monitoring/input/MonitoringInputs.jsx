import { useState } from "react";
import TimeFrame from "./time_frame/TimeFrame";
import TimeFrameSelector from "./time_frame/TimeFrameSelector";
import FeatureSelector from "./feature/FeatureSelector";
import { hasValue, isBlank, nullIfEmpty } from "../../../../common/js/Utils";
import FunctionalitySelector from "./functionality/FunctionalitySelector";
import ServiceSelector from "./service/ServiceSelector";
import Button from "../../../../common/component/input/Button";
import PreLabeledInputField from "common/component/input/PreLabeledInputField";
import InputField from "common/component/input/InputField";

const MonitoringInputs = ({ localizationHandler, setDisplaySpinner, queryData, setQueryData, filterText, setFilterText }) => {
    const [timeFrame, setTimeFrame] = useState(TimeFrame.SECOND);
    const [feature, setFeature] = useState("");
    const [functionality, setFunctionality] = useState("");
    const [service, setService] = useState("");

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
            service: nullIfEmpty(service)
        }
        setQueryData(data);
        setFilterText("");
    }
}

export default MonitoringInputs;