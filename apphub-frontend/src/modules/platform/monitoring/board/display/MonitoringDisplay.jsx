import { useState } from "react";
import Polyline from "../../../../../common/component/svg/Polyline";
import MapStream from "../../../../../common/js/collection/MapStream";
import Stream from "../../../../../common/js/collection/Stream";
import Constants from "../../../../../common/js/Constants";
import LocalDateTime from "../../../../../common/js/date/LocalDateTime";
import { formatNumber, generateRandomId, getColors, hasValue } from "../../../../../common/js/Utils";

const VIEWBOX_HEIGHT = Constants.GRAPH_HEIGHT + Constants.GRAPH_PADDING * 2;

//TODO split
const MonitoringDisplay = ({ firstTimestamp, lastTimestamp, step, feature, functionality, service, metrics }) => {
    console.log(
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
    const entries = getEntries();
    const viewboxWidth = Constants.PIXEL_PER_REPORT * entries.length + Constants.GRAPH_PADDING * 2;
    const colors = getColors(properties);

    return (
        <fieldset className="monitoring-diagram">
            <legend>{feature} - {functionality} - {service}</legend>

            <svg
                id={"monitoring-svg-diagram-" + feature + "-" + functionality + "-" + service}
                className="monitoring-svg-diagram"
                viewBox={"0, 0 " + viewboxWidth + " " + VIEWBOX_HEIGHT}
            >
                {getPropertyLines()}
                {getVerticals()}
            </svg>

            <div>{getLabels()}</div>
        </fieldset>
    );

    function getVerticals() {
        return new Stream(entries)
            .map(entry => entry.timestamp)
            .sorted((a, b) => b - a)
            .map((timestamp, index) => {
                const width = Constants.GRAPH_PADDING + (index * Constants.PIXEL_PER_REPORT);
                return (
                    <line
                        key={timestamp}
                        className={"monitoring-svg-diagram-item-line" + (timestamp == displayedTimestamp ? " active" : "")}
                        x1={width}
                        y1={0}
                        x2={width}
                        y2={VIEWBOX_HEIGHT}
                        onMouseEnter={() => setDisplayedTimestamp(timestamp)}
                    />
                );
            })
            .toList();
    }

    function getLabels() {
        return (
            <div className="monitoring-diagram-labels">
                <span>{LocalDateTime.fromEpochSeconds(displayedTimestamp).format()}</span>
                {getProperties()}
            </div>
        );

        function getProperties() {
            return new MapStream(colors)
                .toList((property, color) =>
                    <span
                        key={property}
                        style={{ color: color.assemble() }}>
                        {property}: {formatNumber(entryMap[displayedTimestamp].properties[property], 3)}
                    </span>
                );
        }
    }

    function getPropertyLines() {
        const maxValue = new Stream(entries)
            .flatMap(entry => new Stream(Object.values(entry.properties)))
            .max()
            .orElseThrow("IllegalArgument", "No properties found for any entry");

        return new Stream(properties)
            .map(property => drawLine(property))
            .toList();

        function drawLine(property) {
            const points = new Stream(entries)
                .sorted((a, b) => b.timestamp - a.timestamp)
                .map((entry, index) => calculateWidth(index) + "," + calculateHeight(entry.properties[property]))
                .join(" ");

            return <Polyline
                key={property}
                className={"monitoring-svg-diagram-item " + property}
                points={points}
                style={{ stroke: colors[property].assemble() }}
            />

            function calculateWidth(index) {
                return Constants.GRAPH_PADDING + (index * Constants.PIXEL_PER_REPORT);
            }

            function calculateHeight(value) {
                if (value == null) {
                    value = 0;
                }

                return Constants.GRAPH_HEIGHT - Math.round(value * Constants.GRAPH_HEIGHT / maxValue) + Constants.GRAPH_PADDING;
            }
        }
    }

    function getEntries() {
        const entries = [];

        for (let timestamp = firstTimestamp; timestamp <= lastTimestamp; timestamp += step) {
            if (hasValue(entryMap[timestamp])) {
                entries.push(entryMap[timestamp]);
            } else {
                entries.push(getClosestMatch(timestamp));
            }
        }

        return entries;

        function getClosestMatch(timestamp) {
            const closest = timestamp + step;
            const timestamps = Object.keys(entryMap);
            for (let i in timestamps) {
                const entryTimestamp = entryMap[timestamps[i]];

                if (Math.abs(entryTimestamp - timestamp) < Math.abs(closest - timestamp)) {
                    closest = entryTimestamp;
                }
            }

            const difference = Math.abs(timestamp - closest);
            const allowedDifference = step / 2;
            if (difference > allowedDifference) {
                const result = {
                    metricDataId: generateRandomId(),
                    feature: feature,
                    functionality: functionality,
                    service: service,
                    timestamp: timestamp,
                    properties: new Stream(properties)
                        .toMap(property => property, () => 0)
                }
                entryMap[timestamp] = result;

                return result;
            }

            return entryMap[closest];
        }
    }
}

export default MonitoringDisplay;