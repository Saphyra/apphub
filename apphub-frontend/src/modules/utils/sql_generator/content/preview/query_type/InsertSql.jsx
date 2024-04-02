import React from "react";
import { assembleTableLocation, findById, getChildrenOf } from "../../util/SegmentUtil";
import Stream from "../../../../../../common/js/collection/Stream";
import SegmentType from "../../../model/SegmentType";

const InsertSql = ({ segments }) => {
    const getColumnNames = () => {
        return new Stream(segments)
            .filter(segment => segment.segmentType === SegmentType.COLUMN_NAME)
            .sorted((a, b) => findById(a.parent, segments).order - findById(b.parent, segments).order)
            .map(segment => segment.value)
            .join(", ");
    }

    const getColumnValues = () => {
        return new Stream(segments)
            .filter(segment => segment.segmentType === SegmentType.COLUMN_VALUE)
            .sorted((a, b) => findById(a.parent, segments).order - findById(b.parent, segments).order)
            .map(segment => "'" + segment.value + "'")
            .join(", ");
    }

    return (
        <div>
            <div>
                <span className="sql-generator-preview-keyword">INSERT INTO</span>
                <span> </span>
                <span className="sql-generator-preview-table-location">{assembleTableLocation(segments)}</span>
            </div>

            <div>
                <span>{"("}</span>
                <span className="sql-generator-preview-column-name">{getColumnNames()}</span>
                <span>{")"}</span>
            </div>

            <div className="sql-generator-preview-keyword">VALUES</div>

            <div>

                <span>{"("}</span>
                <span className="sql-generator-preview-column-value">{getColumnValues()}</span>
                <span>{")"}</span>
            </div>
        </div>
    )
}

export default InsertSql;