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
import NumberColumn from "./type/NumberColumn";
import CheckboxColumn from "./type/CheckboxColumn";
import ColorColumn from "./type/ColorColumn";
import DateColumn from "./type/DateColumn";
import TimeColumn from "./type/TimeColumn";
import DateTimeColumn from "./type/DateTimeColumn";
import MonthColumn from "./type/MonthColumn";
import LinkColumn from "./type/LinkColumn";
import RangeColumn from "./type/RangeColumn";

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
        case ColumnType.NUMBER:
            return <NumberColumn
                columnData={columnData}
                updateColumn={updateColumn}
                editingEnabled={editingEnabled}
                localizationHandler={localizationHandler}
                selectType={() => setDisplayColumnTypeSelector(true)}
            />
        case ColumnType.CHECKBOX:
            return <CheckboxColumn
                columnData={columnData}
                updateColumn={updateColumn}
                editingEnabled={editingEnabled}
                localizationHandler={localizationHandler}
                selectType={() => setDisplayColumnTypeSelector(true)}
            />
        case ColumnType.COLOR:
            return <ColorColumn
                columnData={columnData}
                updateColumn={updateColumn}
                editingEnabled={editingEnabled}
                localizationHandler={localizationHandler}
                selectType={() => setDisplayColumnTypeSelector(true)}
            />
        case ColumnType.DATE:
            return <DateColumn
                columnData={columnData}
                updateColumn={updateColumn}
                editingEnabled={editingEnabled}
                localizationHandler={localizationHandler}
                selectType={() => setDisplayColumnTypeSelector(true)}
            />
        case ColumnType.TIME:
            return <TimeColumn
                columnData={columnData}
                updateColumn={updateColumn}
                editingEnabled={editingEnabled}
                localizationHandler={localizationHandler}
                selectType={() => setDisplayColumnTypeSelector(true)}
            />
        case ColumnType.DATE_TIME:
            return <DateTimeColumn
                columnData={columnData}
                updateColumn={updateColumn}
                editingEnabled={editingEnabled}
                localizationHandler={localizationHandler}
                selectType={() => setDisplayColumnTypeSelector(true)}
            />
        case ColumnType.MONTH:
            return <MonthColumn
                columnData={columnData}
                updateColumn={updateColumn}
                editingEnabled={editingEnabled}
                localizationHandler={localizationHandler}
                selectType={() => setDisplayColumnTypeSelector(true)}
            />
        case ColumnType.LINK:
            return <LinkColumn
                columnData={columnData}
                updateColumn={updateColumn}
                editingEnabled={editingEnabled}
                localizationHandler={localizationHandler}
                selectType={() => setDisplayColumnTypeSelector(true)}
            />
        case ColumnType.RANGE:
            return <RangeColumn
                columnData={columnData}
                updateColumn={updateColumn}
                editingEnabled={editingEnabled}
                localizationHandler={localizationHandler}
                selectType={() => setDisplayColumnTypeSelector(true)}
            />
        default:
            Utils.throwException("IllegalArgument", "Undhandled columnType " + columnData.columnType);
    }
}

export default TableColumn;