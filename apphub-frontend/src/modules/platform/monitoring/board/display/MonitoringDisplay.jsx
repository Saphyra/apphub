import Polyline from "../../../../../common/component/svg/Polyline";
import MapStream from "../../../../../common/js/collection/MapStream";
import Stream from "../../../../../common/js/collection/Stream";
import Constants from "../../../../../common/js/Constants";
import LocalDateTime from "../../../../../common/js/date/LocalDateTime";
import { generateRandomId, getColors, hasValue } from "../../../../../common/js/Utils";

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

    //<Timestamp - Metric>
    const entryMap = new Stream(metrics)
        .toMap(metric => metric.timestamp);
    const entries = getEntries();
    const viewboxWidth = Constants.PIXEL_PER_REPORT * entries.length + Constants.GRAPH_PADDING * 2;
    const properties = new Stream(entries)
        .flatMap(entry => new Stream(Object.keys(entry.properties)))
        .distinct()
        .toList();

    const colors = getColors(properties);

    return (
        <fieldset className="monitoring-diagram">
            <legend>{feature} - {functionality} - {service}</legend>

            <svg
                id={"monitoring-svg-diagram-" + feature + "-" + functionality + "-" + service}
                className="monitoring-svg-diagram"
                viewBox={"0, 0 " + viewboxWidth + " " + VIEWBOX_HEIGHT}
            >
                {getContent()}
            </svg>

            <div>{getColorLabels()}</div>
        </fieldset>
    );

    function getColorLabels() {
        return new MapStream(colors)
            .toList((property, color) => <span style={{ color: color.assemble() }}> {property} </span>);
    }

    function getContent() {
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
                return {
                    metricDataId: generateRandomId(),
                    feature: feature,
                    functionality: functionality,
                    service: service,
                    timestamp: timestamp,
                    properties: {}
                }
            }

            return entryMap[closest];
        }
    }
}

export default MonitoringDisplay;