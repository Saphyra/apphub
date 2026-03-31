import { useEffect, useState } from "react";
import useLoader from "../../../../common/hook/Loader";
import { MONITORING_GET_METRICS } from "../MonitoringEndpoints";
import Stream from "../../../../common/js/collection/Stream";
import MonitoringDisplay from "./display/MonitoringDisplay";
import { HOUR, MINUTE, SECOND } from "../input/time_frame/TimeFrame";
import { throwException } from "../../../../common/js/Utils";

const MonitoringBoard = ({ setDisplaySpinner, localizationHandler, queryData }) => {
    console.log(queryData);

    const [metricsData, setMetricsData] = useState({ queryData: null, metrics: [] });

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
                {getContent()}
            </div>
        );
    }

    function getContent() {
        const min = new Stream(metricsData.metrics)
            .map(metric => metric.timestamp)
            .min()
            .orElseThrow("IllegalState", "Empty metrics should not be rendered");
        const max = new Stream(metricsData.metrics)
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
                    firstTimestamp={min}
                    lastTimestamp={max}
                    feature={group.feature}
                    functionality={group.functionality}
                    service={group.service}
                    metrics={metrics}
                    step={getStep(queryData.timeFrame)}
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