import React from "react";
import localizationData from "./column_type_selector_localization.json";
import "./column_type_selector.css";
import LocalizationHandler from "../../../../../../../common/js/LocalizationHandler";
import Stream from "../../../../../../../common/js/collection/Stream";
import ColumnType from "../type/ColumnType";
import Button from "../../../../../../../common/component/input/Button";

const ColumnTypeSelector = ({ setDisplayColumnTypeSelector, setColumnType }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const getColumnTypes = () => {
        return new Stream(Object.keys(ColumnType))
            .toMapStream(columnType => columnType, columnType => localizationHandler.get("column-type-" + columnType.toLowerCase()))
            .sorted((a, b) => a.value.localeCompare(b.value))
            .map((columnType, label) =>
                <Button
                    key={columnType}
                    className="notebook-table-column-type-selector-column-type"
                    id={"notebook-table-column-type-selector-column-type-" + columnType.toLowerCase()}
                    label={label}
                    onclick={() => setColumnType(columnType)}
                />
            )
            .toList();
    }

    return (
        <div id="notebook-table-column-type-selector">
            <h2 id="notebook-table-column-type-selector-title">{localizationHandler.get("title")}</h2>

            <div id="notebook-table-column-type-selector-column-types">
                {getColumnTypes()}
            </div>

            <div className="centered">
                <Button
                    id="notebook-table-column-type-selector-cancel-button"
                    label={localizationHandler.get("cancel")}
                    onclick={() => setDisplayColumnTypeSelector(false)}
                />
            </div>
        </div>
    );
}

export default ColumnTypeSelector;