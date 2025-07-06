import React from "react";
import MapStream from "../../../common/js/collection/MapStream";
import Stream from "../../../common/js/collection/Stream";
import { formatDuration } from "../../../common/js/Utils";
import Button from "../../../common/component/input/Button";
import { ADMIN_PANEL_PERFORMANCE_REPORTING_DELETE_REPORTS } from "../../../common/js/dao/endpoints/AdminPanelEndpoints";

const PerformanceReportingReports = ({ reports, localizationHandler, loadReports }) => {
    const getContent = () => {
        return new MapStream(reports)
            .toList((topic, topicReports) => <TopicReports
                key={topic}
                topic={topic}
                reports={topicReports}
                localizationHandler={localizationHandler}
                loadReports={loadReports}
            />)
    }

    return (
        <div id="performance-reporting-reports">
            {getContent()}
        </div>
    );
}

const TopicReports = ({ topic, reports, localizationHandler, loadReports }) => {
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
            <legend>
                {topic}

                <Button
                    label={localizationHandler.get("delete-all")}
                    onclick={deleteAll}
                />
            </legend>

            <div className="performance-reproting-reports-topic-reports">
                {getContent()}
            </div>
        </fieldset>
    );

    async function deleteAll() {
        await ADMIN_PANEL_PERFORMANCE_REPORTING_DELETE_REPORTS.createRequest(null, { topic: topic })
            .send();

        loadReports();
    }
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