import ColumnType from "./type/ColumnType";

const getDefaultDataForColumnType = (columnType) => {
    switch (columnType) {
        case ColumnType.NUMBER:
            return {
                value: 0,
                step: 1
            }
        case ColumnType.RANGE:
            return {
                value: 0,
                step: 1,
                min: -10,
                max: 10
            }
        case ColumnType.TEXT:
        case ColumnType.LINK:
        case ColumnType.DATE:
        case ColumnType.TIME:
        case ColumnType.DATE_TIME:
        case ColumnType.MONTH:
            return "";
        case ColumnType.CHECKBOX:
            return false;
        case ColumnType.COLOR:
            return "#000000";
        default:
            return null;
    }
}

export default getDefaultDataForColumnType;