import { useState } from "react";
import SelectInput, { SelectOption } from "../../../../../common/component/input/SelectInput";
import Stream from "../../../../../common/js/collection/Stream";
import useLoader from "../../../../../common/hook/Loader";
import { MONITORING_GET_FEATURES } from "../../MonitoringEndpoints";
import PreLabeledInputField from "../../../../../common/component/input/PreLabeledInputField";

const FeatureSelector = ({ localizationHandler, setDisplaySpinner, feature, setFeature }) => {
    const [features, setFeatures] = useState([]);

    useLoader({
        request: MONITORING_GET_FEATURES.createRequest(),
        mapper: setFeatures,
        setDisplaySpinner: setDisplaySpinner,
        listener: [feature]
    });

    return <PreLabeledInputField
        label={localizationHandler.get("feature")}
        input={<SelectInput
            id="monitoring-feature"
            value={feature}
            onchangeCallback={setFeature}
            options={getOptions()}
        />}
    />

    function getOptions() {
        return new Stream(features)
            .map(feature => new SelectOption(feature, feature))
            .add(new SelectOption("", ""))
            .toList();
    }
}

export default FeatureSelector;