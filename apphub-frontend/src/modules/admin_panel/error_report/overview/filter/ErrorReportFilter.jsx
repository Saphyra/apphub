import React from "react";
import "./error_report_filter.css";
import localizationData from "./error_report_filter_localization.json";
import LocalizationHandler from "../../../../../common/js/LocalizationHandler";
import MapStream from "../../../../../common/js/collection/MapStream";
import PreLabeledInputField from "../../../../../common/component/input/PreLabeledInputField";
import InputField from "../../../../../common/component/input/InputField";
import NumberInput from "../../../../../common/component/input/NumberInput";
import SelectInput, { SelectOption } from "../../../../../common/component/input/SelectInput";
import Button from "../../../../../common/component/input/Button";

const ErrorReportFilter = ({ filterData, setFilterData , searchCallback}) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const updateFilterData = (key, value) => {
        const copy = new MapStream(filterData)
            .add(key, value)
            .toObject();
        setFilterData(copy);
    }

    return (
        <div id="error-report-filter">
            <PreLabeledInputField
                label={localizationHandler.get("service")}
                input={<InputField
                    id="error-report-filter-service"
                    placeholder={localizationHandler.get("service")}
                    value={filterData.service}
                    onchangeCallback={(value) => updateFilterData("service", value)}
                />}
            />

            <PreLabeledInputField
                label={localizationHandler.get("message")}
                input={<InputField
                    id="error-report-filter-message"
                    placeholder={localizationHandler.get("message")}
                    value={filterData.message}
                    onchangeCallback={(value) => updateFilterData("message", value)}
                />}
            />

            <PreLabeledInputField
                label={localizationHandler.get("status-code")}
                input={<NumberInput
                    id="error-report-filter-status-code"
                    placeholder={localizationHandler.get("status-code")}
                    value={filterData.statusCode}
                    onchangeCallback={(value) => updateFilterData("statusCode", value)}
                    min={100}
                    max={999}
                />}
            />

            <PreLabeledInputField
                label={localizationHandler.get("from")}
                input={<InputField
                    id="error-report-filter-from"
                    placeholder={localizationHandler.get("datetime-format")}
                    value={filterData.startTime}
                    onchangeCallback={(value) => updateFilterData("startTime", value)}
                />}
            />

            <PreLabeledInputField
                label={localizationHandler.get("until")}
                input={<InputField
                    id="error-report-filter-until"
                    placeholder={localizationHandler.get("datetime-format")}
                    value={filterData.endTime}
                    onchangeCallback={(value) => updateFilterData("endTime", value)}
                />}
            />

            <PreLabeledInputField
                label={localizationHandler.get("items-per-page")}
                input={<SelectInput
                    id="error-report-filter-items-per-page"
                    value={filterData.pageSize}
                    onchangeCallback={(value) => updateFilterData("pageSize", value)}
                    options={[
                        new SelectOption(10, 10),
                        new SelectOption(25, 25),
                        new SelectOption(50, 50),
                        new SelectOption(100, 100),
                    ]}
                />}
            />

            <PreLabeledInputField
                label={localizationHandler.get("status")}
                input={<SelectInput
                    id="error-report-filter-status"
                    value={filterData.status}
                    onchangeCallback={(value) => updateFilterData("status", value)}
                    options={[
                        new SelectOption(localizationHandler.get("status-all"), ""),
                        new SelectOption(localizationHandler.get("status-unread"), "UNREAD"),
                        new SelectOption(localizationHandler.get("status-read"), "READ"),
                        new SelectOption(localizationHandler.get("status-marked"), "MARKED"),
                    ]}
                />}
            />

            <Button
                id="error-report-filter-search"
                label={localizationHandler.get("search")}
                onclick={searchCallback}
            />
        </div>
    );
}

export default ErrorReportFilter;