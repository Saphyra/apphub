import React from "react";
import Constants from "../../../../common/js/Constants";
import Stream from "../../../../common/js/collection/Stream";
import Polyline from "../../../../common/component/svg/Polyline";

const SvgDiagram = ({ service, availableBytes, duration, reports }) => {
    const viewboxWidth = Constants.PIXEL_PER_REPORT * duration + Constants.GRAPH_BORDER * 2;
    const viewboxHeight = Constants.GRAPH_HEIGHT + Constants.GRAPH_BORDER * 2;

    const drawAllocated = () => {
        const points = new Stream(reports)
            .sorted((a, b) => b.epochSeconds - a.epochSeconds)
            .map(item => item.allocatedMemoryBytes)
            .map((allocatedBytes, index) => calculateWidth(index) + "," + calculateHeight(allocatedBytes))
            .join(" ");

        return <Polyline
            className="memory-monitoring-svg-diagram-item allocated"
            points={points}
        />
    }

    const drawUsed = () => {
        const points = new Stream(reports)
            .sorted((a, b) => b.epochSeconds - a.epochSeconds)
            .map(item => item.usedMemoryBytes)
            .map((allocatedBytes, index) => calculateWidth(index) + "," + calculateHeight(allocatedBytes))
            .join(" ");

        return <Polyline
            className="memory-monitoring-svg-diagram-item used"
            points={points}
        />
    }

    const calculateWidth = (index) => {
        return Constants.GRAPH_BORDER + (index * Constants.PIXEL_PER_REPORT);
    }

    const calculateHeight = (bytes) => {
        const result = Constants.GRAPH_HEIGHT - Math.round(bytes * Constants.GRAPH_HEIGHT / availableBytes) + Constants.GRAPH_BORDER;
        return result;
    }

    return (
        <div className="memory-monitoring-svg-diagram">
            <svg
                id={"memory-monitoring-svg-diagram-" + service}
                className="memory-monitoring-svg-container"
                viewBox={"0 0 " + viewboxWidth + " " + viewboxHeight}
            >
                {drawAllocated()}
                {drawUsed()}
            </svg>
        </div >
    );
}

export default SvgDiagram;