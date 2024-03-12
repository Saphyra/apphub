import React, { useEffect, useState } from "react";
import localizationData from "./error_report_page_selector_localization.json";
import "./error_report_page_selector.css";
import LocalizationHandler from "../../../../../common/js/LocalizationHandler";
import MapStream from "../../../../../common/js/collection/MapStream";
import Button from "../../../../../common/component/input/Button";
import PreLabeledInputField from "../../../../../common/component/input/PreLabeledInputField";
import SelectInput, { SelectOption } from "../../../../../common/component/input/SelectInput";

const ErrorReportPageSelector = ({ filterData, setFilterData, refreshCallback }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [updateInterval, setUpdateInterval] = useState(0);
    const [interval, sInterval] = useState(null);

    useEffect(() => handleAutoRefresh(), [updateInterval]);

    const handleAutoRefresh = () => {
        if (updateInterval == 0) {
            clearInterval(interval);
            return;
        }

        const i = setInterval(refreshCallback, updateInterval);
        sInterval(i);

        return () => clearInterval(i);
    }

    const updatePage = (page) => {
        const copy = new MapStream(filterData)
            .add("page", page)
            .toObject();
        setFilterData(copy);
    }

    return (
        <div id="error-report-page-selector">
            <span id="error-report-page-selector-scrolls">
                <Button
                    id="error-report-page-selector-first-page"
                    label={localizationHandler.get("first-page")}
                    onclick={() => updatePage(1)}
                    disabled={filterData.page === 1}
                />

                <Button
                    id="error-report-page-selector-previous-page"
                    label={localizationHandler.get("previous-page")}
                    onclick={() => updatePage(filterData.page - 1)}
                    disabled={filterData.page === 1}
                />

                <span id="error-report-page-selector-page">{filterData.page}</span>

                <Button
                    id="error-report-page-selector-next-page"
                    label={localizationHandler.get("next-page")}
                    onclick={() => updatePage(filterData.page + 1)}
                />
            </span>

            <span id="error-report-page-selector-auto-refresh">
                <PreLabeledInputField
                    label={localizationHandler.get("auto-refresh")}
                    input={<SelectInput
                        id="error-report-page-selector-auto-refresh-interval"
                        onchangeCallback={setUpdateInterval}
                        options={[
                            new SelectOption(localizationHandler.get("dont-refresh"), 0),
                            new SelectOption(localizationHandler.get("x-seconds", { seconds: 1 }), 1000),
                            new SelectOption(localizationHandler.get("x-seconds", { seconds: 5 }), 5000),
                            new SelectOption(localizationHandler.get("x-seconds", { seconds: 10 }), 10000),
                            new SelectOption(localizationHandler.get("x-seconds", { seconds: 30 }), 30000),
                            new SelectOption(localizationHandler.get("x-seconds", { seconds: 60 }), 60000),
                        ]}
                    />}
                />
            </span>
        </div>
    );
}

export default ErrorReportPageSelector;