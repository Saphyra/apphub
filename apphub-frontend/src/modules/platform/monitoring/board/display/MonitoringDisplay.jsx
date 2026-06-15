import { useState } from "react";
import Stream from "../../../../../common/js/collection/Stream";
import Constants from "../../../../../common/js/Constants";
import LocalDateTime from "../../../../../common/js/date/LocalDateTime";
import { getColors } from "../../../../../common/js/Utils";
import { getEntries, getLabels, getPropertyLines, getVerticals } from "./MonitoringDisplayFunctions";

export const MONITORING_VIEWBOX_HEIGHT = Constants.GRAPH_HEIGHT + Constants.GRAPH_PADDING * 2;

const MonitoringDisplay = ({ firstTimestamp, lastTimestamp, step, feature, functionality, service, metrics, hiddenProperties }) => {
    console.debug(
        "Rendering display",
        {
            feature: feature,
            functionality: functionality,
            service: service,
            metricsCount: metrics.length,
            firstTimestamp: LocalDateTime.fromEpochSeconds(firstTimestamp),
            lastTimestamp: LocalDateTime.fromEpochSeconds(lastTimestamp),
            step: step
        });

    const [displayedTimestamp, setDisplayedTimestamp] = useState(lastTimestamp);

    //<Timestamp - Metric> used for data retrieval
    const entryMap = new Stream(metrics)
        .toMap(metric => metric.timestamp);
    //List of all properties
    const properties = new Stream(metrics)
        .flatMap(entry => new Stream(Object.keys(entry.properties)))
        .distinct()
        .toList();
    const entries = getEntries(firstTimestamp, lastTimestamp, step, entryMap, feature, functionality, service, properties);
    const viewboxWidth = Constants.PIXEL_PER_REPORT * entries.length + Constants.GRAPH_PADDING * 2;
    const colors = getColors(properties);

    return (
        <fieldset className="monitoring-diagram">
            <legend>{feature} - {functionality} - {service}</legend>

            <svg
                id={"monitoring-svg-diagram-" + feature + "-" + functionality + "-" + service}
                className="monitoring-svg-diagram"
                viewBox={"0, 0 " + viewboxWidth + " " + MONITORING_VIEWBOX_HEIGHT}
            >
                {getPropertyLines(entries, properties, colors, hiddenProperties)}
                {getVerticals(entries, displayedTimestamp, setDisplayedTimestamp)}
            </svg>

            <div>{getLabels(displayedTimestamp, entryMap, colors)}</div>
        </fieldset>
    );


}

export default MonitoringDisplay;