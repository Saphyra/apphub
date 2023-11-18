import React, { useState } from "react";
import ColumnType from "./type/ColumnType";
import TextColumn from "./type/TextColumn";
import Utils from "../../../../../../common/js/Utils";
import localizationData from "./table_column_localization.json";
import LocalizationHandler from "../../../../../../common/js/LocalizationHandler";
import "./table_column.css";
import ColumnTypeSelector from "./column_type_selector/ColumnTypeSelector";
import getDefaultDataForColumnType from "./DefaultColumnValueProvider";
import EmptyColumn from "./type/EmptyColumn";

const TableColumn = ({ columnData, updateColumn, editingEnabled = true, custom = false }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [displayColumnTypeSelector, setDisplayColumnTypeSelector] = useState(false);

    const setColumnType = (columnType) => {
        columnData.columnType = columnType;
        columnData.data = getDefaultDataForColumnType(columnType);
        setDisplayColumnTypeSelector(false);

        updateColumn();
    }

    if (displayColumnTypeSelector) {
        return <ColumnTypeSelector
            setColumnType={setColumnType}
            setDisplayColumnTypeSelector={setDisplayColumnTypeSelector}
        />
    }

    switch (columnData.columnType) {
        case ColumnType.EMPTY:
            return <EmptyColumn
                editingEnabled={editingEnabled}
                selectType={() => setDisplayColumnTypeSelector(true)}
                localizationHandler={localizationHandler}
            />
        case ColumnType.TEXT:
            return <TextColumn
                columnData={columnData}
                updateColumn={updateColumn}
                editingEnabled={editingEnabled}
                custom={custom}
                localizationHandler={localizationHandler}
                selectType={() => setDisplayColumnTypeSelector(true)}
            />
        default:
            Utils.throwException("IllegalArgument", "Undhandled columnType " + columnData.columnType);
    }
}

export default TableColumn;