import { useState } from "react";
import TimeFrame from "./time_frame/TimeFrame";
import TimeFrameSelector from "./time_frame/TimeFrameSelector";
import FeatureSelector from "./feature/FeatureSelector";
import { isBlank, nullIfEmpty } from "../../../../common/js/Utils";
import FunctionalitySelector from "./functionality/FunctionalitySelector";
import ServiceSelector from "./service/ServiceSelector";
import Button from "../../../../common/component/input/Button";

const MonitoringInputs = ({ localizationHandler, setDisplaySpinner, setQueryData }) => {
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
    }
}

export default MonitoringInputs;