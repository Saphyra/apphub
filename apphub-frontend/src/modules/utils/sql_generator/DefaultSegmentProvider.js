import Utils from "../../../common/js/Utils";
import QueryType from "./model/QueryType";
import Segment from "./model/Segment";
import SegmentType from "./model/SegmentType";

const getDefaultSegmentsFor = (queryType) => {
    switch (queryType) {
        case QueryType.INSERT:
            const tableLocation = new Segment(SegmentType.TABLE_LOCATION, null, 0);
            const schemaName = new Segment(SegmentType.SCHEMA_NAME, tableLocation.id, 0, "");
            const tableName = new Segment(SegmentType.TABLE_NAME, tableLocation.id, 1, "");
            const columnValuePairs = new Segment(SegmentType.COLUMN_VALUE_PAIRS, null, 1);
            const columnValuePair = new Segment(SegmentType.COLUMN_VALUE_PAIR, columnValuePairs.id, 0);
            const columnName = new Segment(SegmentType.COLUMN_NAME, columnValuePair.id, 0, "");
            const columnValue = new Segment(SegmentType.COLUMN_VALUE, columnValuePair.id, 1, "");

            return [
                tableLocation,
                schemaName,
                tableName,
                columnValuePairs,
                columnValuePair,
                columnName,
                columnValue
            ];
        default:
            Utils.throwException("IllegalArgument", "Cannot create default segments for QueryType " + queryType);
    }
}

export default getDefaultSegmentsFor;