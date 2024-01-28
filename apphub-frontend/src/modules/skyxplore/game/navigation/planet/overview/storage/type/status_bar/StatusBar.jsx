import React from "react";
import "./status_bar.css";

const StatusBar = ({ capacity, actualAmount, allocatedAmount, reservedAmount }) => {
    const allocatedPercentage = allocatedAmount / capacity * 100
    const actualPercentage = actualAmount / capacity * 100 - allocatedPercentage;
    const reservedPercentage = reservedAmount / capacity * 100;

    return (
        <div className="skyxplore-game-planet-overview-storage-status-bar">
            <div
                className="skyxplore-game-planet-overview-storage-status-bar-actual"
                style={{
                    width: actualPercentage + "%",
                }}
            />

            <div
                className="skyxplore-game-planet-overview-storage-status-bar-allocated"
                style={{
                    width: allocatedPercentage + "%",
                    left: actualPercentage + "%"
                }}
            />

            <div
                className="skyxplore-game-planet-overview-storage-status-bar-reserved"
                style={{
                    width: reservedPercentage + "%",
                    left: (actualPercentage + allocatedPercentage) + "%"
                }}
            />
        </div>
    )
}

export default StatusBar;