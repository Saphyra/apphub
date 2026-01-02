import React from "react";
import "./progress_bar.css";

const ProgressBar = ({ id, className, currentPoints, targetPoints, content, operations, displayPercentage = true }) => {
    const width = Math.floor(currentPoints / targetPoints * 100);

    return (
        <div id={id} className={"progress-bar " + className}>
            <div
                className="progress-bar-progress"
                style={{
                    width: width + "%"
                }}
            />

            <div className="progress-bar-content">
                {content}
                {displayPercentage &&
                    <span className="progress-bar-content-percentage-wrapper">
                        <span className="progress-bar-content-percentage">{width}</span>
                        <span> %</span>
                    </span>
                }
                {operations}
            </div>
        </div>
    );
}

export default ProgressBar;