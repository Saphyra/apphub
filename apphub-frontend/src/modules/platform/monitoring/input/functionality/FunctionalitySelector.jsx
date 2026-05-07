import { useState } from "react";
import { MONITORING_GET_FUNCTIONALITIES } from "../../MonitoringEndpoints";
import useLoader from "../../../../../common/hook/Loader";
import PreLabeledInputField from "../../../../../common/component/input/PreLabeledInputField";
import SelectInput, { SelectOption } from "../../../../../common/component/input/SelectInput";
import Stream from "../../../../../common/js/collection/Stream";

const FunctionalitySelector = ({ localizationHandler, setDisplaySpinner, functionality, setFunctionality, feature }) => {
    const [functionalities, setFunctionalities] = useState([]);

    useLoader({
        request: MONITORING_GET_FUNCTIONALITIES.createRequest(null, { feature: feature }),
        mapper: setFunctionalities,
        setDisplaySpinner: setDisplaySpinner,
        listener: [feature]
    });

    return <PreLabeledInputField
        label={localizationHandler.get("functionality")}
        input={<SelectInput
            id="monitoring-functionality"
            value={functionality}
            onchangeCallback={setFunctionality}
            options={getOptions()}
        />}
    />

    function getOptions() {
        return new Stream(functionalities)
            .sorted((a, b) => a.localeCompare(b))
            .map(func => new SelectOption(func, func))
            .add(new SelectOption("", ""))
            .toList();
    }
}

export default FunctionalitySelector;