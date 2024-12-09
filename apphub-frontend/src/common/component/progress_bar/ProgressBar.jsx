import React from "react";
import "./progress_bar.css";

const ProgressBar = ({ id, currentPoints, targetPoints, content, displayPercentage = true }) => {
    const width = Math.floor(currentPoints / targetPoints * 100);

    return (
        <div id={id} className="progress-bar">
            <div
                className="progress-bar-progress"
                style={{
                    width: width + "%"
                }}
            />

            <div className="progress-bar-content">
                {content}
                {displayPercentage &&
                    <div className="progress-bar-content-percentage">
                        {width} %
                    </div>
                }
            </div>
        </div>
    );
}

export default ProgressBar;