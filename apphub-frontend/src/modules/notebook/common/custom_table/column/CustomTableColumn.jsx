import React from "react";
import CustomTableColumnType from "../CustomTableColumnType";
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
            case CustomTableColumnType.EMPTY:
                return <Empty
                    editingEnabled={editingEnabled}
                    selectType={selectType}
                    localizationHandler={localizationHandler}
                />
            case CustomTableColumnType.TEXT:
            case CustomTableColumnType.LINK:
                return <Text
                    content={columnData.data}
                    updateContent={updateData}
                    editingEnabled={editingEnabled}
                    selectType={selectType}
                    localizationHandler={localizationHandler}
                />
            case CustomTableColumnType.CHECKBOX:
                return <Checkbox
                    checked={columnData.data}
                    updateData={updateData}
                    selectType={selectType}
                    localizationHandler={localizationHandler}
                />
            case CustomTableColumnType.NUMBER:
                return <Number
                    data={columnData.data}
                    updateData={updateData}
                    selectType={selectType}
                    localizationHandler={localizationHandler}
                />
            case CustomTableColumnType.RANGE:
                return <Range
                    data={columnData.data}
                    updateData={updateData}
                    selectType={selectType}
                    localizationHandler={localizationHandler}
                />
            case CustomTableColumnType.COLOR:
                return <Color
                    data={columnData.data}
                    updateData={updateData}
                    selectType={selectType}
                    localizationHandler={localizationHandler}
                />
            case CustomTableColumnType.DATE:
                return <Date
                    data={columnData.data}
                    updateData={updateData}
                    selectType={selectType}
                    localizationHandler={localizationHandler}
                />
            case CustomTableColumnType.TIME:
                return <Time
                    data={columnData.data}
                    updateData={updateData}
                    selectType={selectType}
                    localizationHandler={localizationHandler}
                />
            case CustomTableColumnType.DATE_TIME:
                return <DateTime
                    data={columnData.data}
                    updateData={updateData}
                    selectType={selectType}
                    localizationHandler={localizationHandler}
                />
            case CustomTableColumnType.MONTH:
                return <Month
                    data={columnData.data}
                    updateData={updateData}
                    selectType={selectType}
                    localizationHandler={localizationHandler}
                />
            case CustomTableColumnType.FILE:
                return <File
                    updateData={updateData}
                    selectType={selectType}
                    localizationHandler={localizationHandler}
                />
            case CustomTableColumnType.IMAGE:
                return <Image
                    data={columnData.data}
                    updateData={updateData}
                    selectType={selectType}
                    localizationHandler={localizationHandler}
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