import React from "react";
import Stream from "../../../../common/js/collection/Stream";
import Utils from "../../../../common/js/Utils";
import SvgDiagram from "./SvgDiagram";

const Diagram = ({ service, reports, duration, localizationHandler }) => {
    const lastReport = new Stream(reports)
        .sorted((a, b) => b.epochSeconds - a.epochSeconds)
        .findFirst()
        .orElse(null);

    if (lastReport == null) {
        return;
    }

    const availableBytes = new Stream(reports)
        .map(report => report.availableMemoryBytes)
        .max()
        .orElse(0);

    return (
        <div
            id={"memory-monitoring-diagram-" + service}
            className="memory-monitoring-diagram"
        >
            <div className="memory-monitoring-diagram-title">{service}</div>

            <div className="memory-monitoring-diagram-content">
                <div className="memory-monitoring-diagram-details">
                    <div className="memory-monitoring-available-memory">
                        <span>{localizationHandler.get("available")}: </span>
                        <span>{Utils.bytesToMegabytes(availableBytes)} MB</span>
                    </div>

                    <div className="memory-monitoring-allocated-memory">
                        <span>{localizationHandler.get("allocated")}: </span>
                        <span>{Utils.bytesToMegabytes(lastReport.allocatedMemoryBytes)} MB</span>
                    </div>

                    <div className="memory-monitoring-used-memory">
                        <span>{localizationHandler.get("used")}: </span>
                        <span>{Utils.bytesToMegabytes(lastReport.usedMemoryBytes)} MB</span>
                    </div>
                </div>

                <SvgDiagram
                    service={service}
                    availableBytes={availableBytes}
                    duration={duration}
                    reports={reports}
                />
            </div>
        </div>
    );
}

export default Diagram;