import React from "react";
import Utils from "../../../../../common/js/Utils";
import "./custom_table_column.css";
import localizationData from "./custom_table_column_localization.json";
import LocalizationHandler from "../../../../../common/js/LocalizationHandler";
import Empty from "./type/Empty";
import Text from "./type/Text";
import Checkbox from "./type/Checkbox";
import Number from "./type/Number";
import Color from "./type/Color";
import Range from "./type/Range";
import Date from "./type/Date";
import Time from "./type/Time";
import DateTime from "./type/DateTime";
import Month from "./type/Month";
import File from "./type/File";
import Image from "./type/Image";
import ColumnType from "../../table/row/column/type/ColumnType";

const CustomTableColumn = ({
    columnData,
    updateColumn,
    setColumnTypeSelectorData,
    editingEnabled = true
}) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const updateData = (newValue) => {
        columnData.data = newValue;
        updateColumn();
    }

    const selectType = () => {
        setColumnTypeSelectorData(columnData.columnIndex);
    }

    const getColumnContent = () => {
        switch (columnData.type) {
            case ColumnType.EMPTY:
                return <Empty
                    editingEnabled={editingEnabled}
                    selectType={selectType}
                    localizationHandler={localizationHandler}
                />
            case ColumnType.TEXT:
            case ColumnType.LINK:
                return <Text
                    content={columnData.data}
                    updateContent={updateData}
                    editingEnabled={editingEnabled}
                    selectType={selectType}
                    localizationHandler={localizationHandler}
                />
            case ColumnType.CHECKBOX:
                return <Checkbox
                    checked={columnData.data}
                    updateData={updateData}
                    selectType={selectType}
                    localizationHandler={localizationHandler}
                />
            case ColumnType.NUMBER:
                return <Number
                    data={columnData.data}
                    updateData={updateData}
                    selectType={selectType}
                    localizationHandler={localizationHandler}
                />
            case ColumnType.RANGE:
                return <Range
                    data={columnData.data}
                    updateData={updateData}
                    selectType={selectType}
                    localizationHandler={localizationHandler}
                />
            case ColumnType.COLOR:
                return <Color
                    data={columnData.data}
                    updateData={updateData}
                    selectType={selectType}
                    localizationHandler={localizationHandler}
                />
            case ColumnType.DATE:
                return <Date
                    data={columnData.data}
                    updateData={updateData}
                    selectType={selectType}
                    localizationHandler={localizationHandler}
                />
            case ColumnType.TIME:
                return <Time
                    data={columnData.data}
                    updateData={updateData}
                    selectType={selectType}
                    localizationHandler={localizationHandler}
                />
            case ColumnType.DATE_TIME:
                return <DateTime
                    data={columnData.data}
                    updateData={updateData}
                    selectType={selectType}
                    localizationHandler={localizationHandler}
                />
            case ColumnType.MONTH:
                return <Month
                    data={columnData.data}
                    updateData={updateData}
                    selectType={selectType}
                    localizationHandler={localizationHandler}
                />
            case ColumnType.FILE:
                return <File
                    updateData={updateData}
                    selectType={selectType}
                    localizationHandler={localizationHandler}
                />
            case ColumnType.IMAGE:
                return <Image
                    data={columnData.data}
                    updateData={updateData}
                    selectType={selectType}
                    localizationHandler={localizationHandler}
                    editingEnabled={editingEnabled}
                />
            default:
                Utils.throwException("IllegalArgument", "Unhandled CustomTableColumnType: " + columnData.type);
        }
    }

    return (
        <td className={"notebook-custom-table-column-type-" + columnData.type.toLowerCase()}>
            {getColumnContent()}
        </td>
    )
}

export default CustomTableColumn;