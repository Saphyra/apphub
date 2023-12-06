import React from "react";
import MapStream from "../../../../common/js/collection/MapStream";
import Switch from "./Switch";

const Switches = ({ services, setServices }) => {
    const getSwitches = () => {
        return new MapStream(services)
            .sorted((a, b) => a.key.localeCompare(b.key))
            .toList((service, shouldDisplay) =>
                <Switch
                    key={service}
                    service={service}
                    shouldDisplay={shouldDisplay}
                    setService={setService}
                />
            );
    }

    const setService = (service, shouldDisplay) => {
        services[service] = shouldDisplay;
        setServices(services);
    }

    const setAll = (value) => {
        const copy = new MapStream(services)
            .map((service, shouldDisplay) => value)
            .toObject();

        setServices(copy);
    }

    return (
        <div id="memory-monitoring-switches">
            <Switch
                id="memory-monitoring-show-all-button"
                service={"Show all"}
                shouldDisplay={true}
                setService={() => setAll(true)}
            />

            <Switch
                id="memory-monitoring-hide-all-button"
                service={"Hide all"}
                shouldDisplay={true}
                setService={() => setAll(false)}
            />

            {getSwitches()}
        </div>
    );
}

export default Switches;