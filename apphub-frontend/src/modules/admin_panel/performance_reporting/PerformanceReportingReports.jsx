import React from "react";
import MapStream from "../../../common/js/collection/MapStream";
import Stream from "../../../common/js/collection/Stream";
import { formatDuration } from "../../../common/js/Utils";

const PerformanceReportingReports = ({ reports, localizationHandler }) => {
    const getContent = () => {
        return new MapStream(reports)
            .toList((topic, topicReports) => <TopicReports
                key={topic}
                topic={topic}
                reports={topicReports}
                localizationHandler={localizationHandler}
            />)
    }

    return (
        <div id="performance-reporting-reports">
            {getContent()}
        </div>
    );
}

const TopicReports = ({ topic, reports, localizationHandler }) => {
    const getContent = () => {
        return new Stream(reports)
            .sorted((a, b) => a.key.localeCompare(b.key))
            .map(report => <TopicReportItem
                key={report.key}
                report={report}
                localizationHandler={localizationHandler}
            />)
            .toList();
    }


    return (
        <fieldset className={"performance-reporting-reports-topic " + topic}>
            <legend>{topic}</legend>

            <div className="performance-reproting-reports-topic-reports">
                {getContent()}
            </div>
        </fieldset>
    );
}

const TopicReportItem = ({ report, localizationHandler }) => {
    return (
        <div>
            <table className="formatted-table">
                <thead>
                    <tr>
                        <td colSpan={2}>{report.key}</td>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>{localizationHandler.get("min")}</td>
                        <td>{formatDuration(report.min)}</td>
                    </tr>
                    <tr>
                        <td>{localizationHandler.get("max")}</td>
                        <td>{formatDuration(report.max)}</td>
                    </tr>
                    <tr>
                        <td>{localizationHandler.get("avg")}</td>
                        <td>{formatDuration(report.average)}</td>
                    </tr>
                    <tr>
                        <td>{localizationHandler.get("count")}</td>
                        <td>{report.count.toLocaleString()}</td>
                    </tr>
                </tbody>
            </table>
        </div>
    )
}

export default PerformanceReportingReports;