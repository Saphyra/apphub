import React, { useState } from "react";
import MapStream from "../../../common/js/collection/MapStream";
import Stream from "../../../common/js/collection/Stream";
import { formatDuration } from "../../../common/js/Utils";
import Button from "../../../common/component/input/Button";
import { ADMIN_PANEL_PERFORMANCE_REPORTING_DELETE_REPORTS } from "../../../common/js/dao/endpoints/AdminPanelEndpoints";
import InputField from "../../../common/component/input/InputField";
import PreLabeledInputField from "../../../common/component/input/PreLabeledInputField";
import SelectInput, { SelectOption } from "../../../common/component/input/SelectInput";

const KEY = "key";
const MINIMUM = "minimum";
const MAXIMUM = "maximum";
const AVERAGE = "average";
const COUNT = "count";
const ASCENDING = "ascending";
const DESCENDING = "descending";

const ORDER_BY = {}
ORDER_BY[KEY] = (a, b) => a.key.localeCompare(b.key);
ORDER_BY[MINIMUM] = (a, b) => a.min - b.min;
ORDER_BY[MAXIMUM] = (a, b) => a.max - b.max;
ORDER_BY[AVERAGE] = (a, b) => a.average - b.average;
ORDER_BY[COUNT] = (a, b) => a.count - b.count;

const DIRECTION = {};
DIRECTION[ASCENDING] = value => value;
DIRECTION[DESCENDING] = value => -1 * value;

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
    const [searchText, setSearchText] = useState("");
    const [orderBy, setOrderBy] = useState(KEY);
    const [direction, setDirection] = useState(ASCENDING);

    return (
        <fieldset className={"performance-reporting-reports-topic " + topic}>
            <legend>
                {topic}

                <Button
                    label={localizationHandler.get("delete-all")}
                    onclick={deleteAll}
                />

                <InputField
                    type="text"
                    placeholder={localizationHandler.get("search-text")}
                    onchangeCallback={setSearchText}
                    value={searchText}
                />

                <PreLabeledInputField
                    label={localizationHandler.get("order-by")}
                    input={<SelectInput
                        value={orderBy}
                        options={getOrderByOptions()}
                        onchangeCallback={setOrderBy}
                    />
                    }
                />

                <SelectInput
                    value={direction}
                    options={getDirectionOptions()}
                    onchangeCallback={setDirection}
                />
            </legend>

            <div className="performance-reproting-reports-topic-reports">
                {getContent()}
            </div>
        </fieldset>
    );

    function getDirectionOptions() {
        return new Stream(Object.keys(DIRECTION))
            .toMapStream(key => key, key => localizationHandler.get(key))
            .sorted((a, b) => a.value.localeCompare(b.value))
            .toList((key, label) => new SelectOption(label, key));
    }

    function getOrderByOptions() {
        return new Stream(Object.keys(ORDER_BY))
            .toMapStream(key => key, key => localizationHandler.get(key))
            .sorted((a, b) => a.value.localeCompare(b.value))
            .toList((key, label) => new SelectOption(label, key));
    }

    function getContent() {
        return new Stream(reports)
            .sorted((a, b) => DIRECTION[direction](ORDER_BY[orderBy](a, b)))
            .filter(report => report.key.toLowerCase().indexOf(searchText.toLowerCase()) > -1)
            .map(report => <TopicReportItem
                key={report.key}
                report={report}
                localizationHandler={localizationHandler}
            />)
            .toList();
    }

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