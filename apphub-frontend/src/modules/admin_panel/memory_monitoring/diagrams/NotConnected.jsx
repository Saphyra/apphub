import React from "react";

const NotConnected = ({ localizationHandler }) => {
    return (
        <div className="memory-monitoring-not-connected">
            {localizationHandler.get("not-connected")}
        </div>
    )
}

export default NotConnected;