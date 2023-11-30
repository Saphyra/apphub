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
import FileColumn from "./type/FileColumn";
import ImageColumn from "./type/ImageColumn";

const TableColumn = ({ columnData, updateColumn, editingEnabled = true, custom = false, addFileToColum }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [displayColumnTypeSelector, setDisplayColumnTypeSelector] = useState(false);

    const setColumnType = (columnType) => {
        columnData.columnType = columnType;
        columnData.data = getDefaultDataForColumnType(columnType);
        setDisplayColumnTypeSelector(false);

        updateColumn();
    }

    if (displayColumnTypeSelector) {
        return (
            <td>
                <ColumnTypeSelector
                    setColumnType={setColumnType}
                    setDisplayColumnTypeSelector={setDisplayColumnTypeSelector}
                />
            </td>
        )
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
        case ColumnType.FILE:
            return <FileColumn
                columnData={columnData}
                updateColumn={updateColumn}
                editingEnabled={editingEnabled}
                localizationHandler={localizationHandler}
                selectType={() => setDisplayColumnTypeSelector(true)}
                addFileToColum={addFileToColum}
            />
        case ColumnType.IMAGE:
            return <ImageColumn
                columnData={columnData}
                updateColumn={updateColumn}
                editingEnabled={editingEnabled}
                localizationHandler={localizationHandler}
                selectType={() => setDisplayColumnTypeSelector(true)}
                addFileToColum={addFileToColum}
            />
        default:
            Utils.throwException("IllegalArgument", "Undhandled columnType " + columnData.columnType);
    }
}

export default TableColumn;