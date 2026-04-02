import { useState } from "react";
import PreLabeledInputField from "../../../../../common/component/input/PreLabeledInputField";
import { nullIfEmpty } from "../../../../../common/js/Utils";
import useLoader from "../../../../../common/hook/Loader";
import { MONITORING_GET_SERVICES } from "../../MonitoringEndpoints";
import SelectInput, { SelectOption } from "../../../../../common/component/input/SelectInput";
import Stream from "../../../../../common/js/collection/Stream";

const ServiceSelector = ({ localizationHandler, setDisplaySpinner, service, setService, feature, functionality }) => {
    const [services, setServices] = useState([]);

    useLoader({
        request: MONITORING_GET_SERVICES.createRequest(null, { feature: feature }, { functionality: nullIfEmpty(functionality) }),
        mapper: setServices,
        setDisplaySpinner: setDisplaySpinner,
        listener: [feature, functionality]
    });

    return <PreLabeledInputField
        label={localizationHandler.get("service")}
        input={<SelectInput
            id="monitoring-service"
            value={service}
            onchangeCallback={setService}
            options={getOptions()}
        />}
    />

    function getOptions() {
        return new Stream(services)
            .sorted((a, b) => a.localeCompare(b))
            .map(service => new SelectOption(service, service))
            .add(new SelectOption("", ""))
            .toList();
    }
}

export default ServiceSelector;