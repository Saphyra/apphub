import React from "react";
import "./citizen_status_bar.css";

const CitizenStatusBar = ({ type, label, value, max }) => {
    const width = value / max * 100;

    return (
        <div className={"skyxplore-game-population-citizen-status-bar skyxplore-game-population-citizen-status-bar-" + type}>
            <div
                className="skyxplore-game-population-citizen-status-bar-bar"
                style={{
                    width: width + "%"
                }}
            />

            <div className="skyxplore-game-population-citizen-status-bar-label">
                {label}
            </div>
        </div>
    );
};

export default CitizenStatusBar;