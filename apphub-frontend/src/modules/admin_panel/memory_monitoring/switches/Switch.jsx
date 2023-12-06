import React from "react";
import Button from "../../../../common/component/input/Button";

const Switch = ({ service, shouldDisplay, setService, id = "memory-monitoring-toggle-" + service + "-button" }) => {
    return (
        <Button
            id={id}
            className={"memory-monitoring-switch " + (shouldDisplay ? "shown-service" : "hidden-service")}
            label={service}
            onclick={() => setService(service, !shouldDisplay)}
        />
    );
}

export default Switch;