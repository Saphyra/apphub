import { useState } from "react";
import useLoader from "../../../../common/hook/Loader";
import { MONITORING_GET_METRICS } from "../MonitoringEndpoints";
import Stream from "../../../../common/js/collection/Stream";
import MonitoringDisplay from "./display/MonitoringDisplay";
import { HOUR, MINUTE, SECOND } from "../input/time_frame/TimeFrame";
import { throwException } from "../../../../common/js/Utils";
import PropertyFilter from "./filter/PropertyFilter";

const MonitoringBoard = ({ setDisplaySpinner, localizationHandler, queryData }) => {
    console.log(queryData);

    const [metricsData, setMetricsData] = useState({ queryData: null, metrics: [] });
    const [hiddenProperties, setHiddenProperties] = useState([]);

    useLoader({
        request: MONITORING_GET_METRICS.createRequest(
            null,
            { type: queryData.timeFrame },
            { feature: queryData.feature, functionality: queryData.functionality, service: queryData.service }
        ),
        mapper: response => setMetricsData({ queryData: queryData, metrics: response }),
        setDisplaySpinner: setDisplaySpinner,
        listener: [queryData]
    });

    if (metricsData.queryData == queryData && metricsData.metrics.length > 0) {
        return (
            <div id="monitoring-boards">
                <PropertyFilter
                    metrics={metricsData.metrics}
                    hiddenProperties={hiddenProperties}
                    setHiddenProperties={setHiddenProperties}
                />

                {getContent()}
            </div>
        );
    }

    function getContent() {
        const minTimestamp = new Stream(metricsData.metrics)
            .map(metric => metric.timestamp)
            .min()
            .orElseThrow("IllegalState", "Empty metrics should not be rendered");
        const maxTimestamp = new Stream(metricsData.metrics)
            .map(metric => metric.timestamp)
            .max()
            .orElseThrow("IllegalState", "Empty metrics should not be rendered");

        return new Stream(metricsData.metrics)
            .groupBy(metric => JSON.stringify({ feature: metric.feature, functionality: metric.functionality, service: metric.service }))
            .sorted((a, b) => a.key.localeCompare(b.key))
            .toList((groupId, metrics) => {
                const group = JSON.parse(groupId);

                return <MonitoringDisplay
                    key={groupId}
                    firstTimestamp={minTimestamp}
                    lastTimestamp={maxTimestamp}
                    feature={group.feature}
                    functionality={group.functionality}
                    service={group.service}
                    metrics={metrics}
                    step={getStep(queryData.timeFrame)}
                    hiddenProperties={hiddenProperties}
                />
            });

        function getStep(timeFrame) {
            switch (timeFrame) {
                case SECOND:
                    return 1;
                case MINUTE:
                    return 60;
                case HOUR:
                    return 3600;
                default:
                    throwException("IllegalArgument", "Unhandled time frame: " + timeFrame);
            }
        }
    }
}

export default MonitoringBoard;