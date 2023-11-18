import React from "react";
import Header from "../../../../../common/component/Header";
import localizationData from "./custom_table_column_type_selector_localization.json";
import LocalizationHandler from "../../../../../common/js/LocalizationHandler";
import Footer from "../../../../../common/component/Footer";
import Button from "../../../../../common/component/input/Button";
import Stream from "../../../../../common/js/collection/Stream";
import "./custom_table_column_type_selector.css";
import ColumnType from "../../table/row/column/type/ColumnType";

const CustomTableColumnTypeSelector = ({ columnTypeSelectorData, setColumnTypeSelectorData, setColumnType }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const getColumnTypes = () => {
        return new Stream(Object.keys(ColumnType))
            .toMapStream(columnType => columnType, columnType => localizationHandler.get("column-type-" + columnType.toLowerCase()))
            .sorted((a, b) => a.value.localeCompare(b.value))
            .map((columnType, label) =>
                <Button
                    key={columnType}
                    className="custom-table-column-type-selector-column-type"
                    id={"custom-table-column-type-selector-column-type-" + columnType.toLowerCase()}
                    label={label}
                    onclick={() => setColumnType(columnTypeSelectorData.rowIndex, columnTypeSelectorData.columnIndex, columnType)}
                />
            )
            .toList();
    }

    return (
        <div id="notebook-custom-table-column-type-selector">
            <Header label={localizationHandler.get("title")} />

            <main id="notebook-custom-table-column-type-selector-main">
                {getColumnTypes()}
            </main>

            <Footer
                centerButtons={
                    <Button
                        id="notebook-custom-table-column-type-selector-cancel-button"
                        label={localizationHandler.get("cancel")}
                        onclick={() => setColumnTypeSelectorData(null)}
                    />
                }
            />
        </div>
    );
}

export default CustomTableColumnTypeSelector;