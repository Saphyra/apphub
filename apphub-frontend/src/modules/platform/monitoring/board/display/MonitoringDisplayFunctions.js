import Polyline from "../../../../../common/component/svg/Polyline";
import MapStream from "../../../../../common/js/collection/MapStream";
import Stream from "../../../../../common/js/collection/Stream";
import Constants from "../../../../../common/js/Constants";
import LocalDateTime from "../../../../../common/js/date/LocalDateTime";
import { formatNumber, generateRandomId, hasValue } from "../../../../../common/js/Utils";
import { MONITORING_VIEWBOX_HEIGHT } from "./MonitoringDisplay";

/**
 * Assembles the entries to be displayed in the diagram.
 * If an entry for a timestamp is missing, the closest match is used if it is not too far away.
 * Otherwise, an empty entry is created.
 * @param {*} firstTimestamp chronologically first timestamp to be displayed in the diagram
 * @param {*} lastTimestamp  chronologically last timestamp to be displayed in the diagram
 * @param {*} step  time between two entries in seconds
 * @param {*} entryMap  map of timestamps to entries
 * @param {*} feature  feature name
 * @param {*} functionality  functionality name
 * @param {*} service  service name
 * @param {*} properties  of the entries
 * @returns an array of entries, with gaps filled by empty values
 */
export const getEntries = (firstTimestamp, lastTimestamp, step, entryMap, feature, functionality, service, properties) => {
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

/**
 * Draws the vertical lines for each entry and highlights the one for the currently displayed timestamp.
 * @param {*} entries  list of entries to be displayed in the diagram
 * @param {*} displayedTimestamp  timestamp of the currently displayed entry
 * @param {*} setDisplayedTimestamp  function to set the currently displayed timestamp
 * @returns the vertical lines for each entry, with the one for the currently displayed timestamp highlighted
 */
export const getVerticals = (entries, displayedTimestamp, setDisplayedTimestamp) => {
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
                    y2={MONITORING_VIEWBOX_HEIGHT}
                    onMouseEnter={() => setDisplayedTimestamp(timestamp)}
                />
            );
        })
        .toList();
}

/**
 * Draws the labels for the currently displayed entry, including the timestamp and property values with the corresponding colors.
 * @param {*} displayedTimestamp  timestamp of the currently displayed entry
 * @param {*} entryMap  map of timestamps to entries
 * @param {*} colors  map of properties to colors
 * @returns  the labels for the currently displayed entry
 */
export const getLabels = (displayedTimestamp, entryMap, colors) => {
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

/**
 * Draws the lines for each property, representing their values over time.
 * @param {*} entries  list of entries to be displayed in the diagram
 * @param {*} properties  list of properties to be displayed
 * @param {*} colors  map of properties to colors
 * @returns  the lines for each property, representing their values over time
 */
export const getPropertyLines = (entries, properties, colors) => {
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