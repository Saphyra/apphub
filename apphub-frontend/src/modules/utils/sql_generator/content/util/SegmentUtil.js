import Utils from "../../../../../common/js/Utils";
import Stream from "../../../../../common/js/collection/Stream"
import SegmentType from "../../model/SegmentType";

export const findBySegmentType = (
    segments = Utils.throwException("IllegalArgument", "segments must not be null"),
    segmentType = Utils.throwException("IllegalArgument", "segmentType must not be null")
) => {
    return new Stream(segments)
        .filter(s => s.segmentType == segmentType)
        .findFirst()
        .orElseThrow("IllegalArgument", "Segment not found with type " + segmentType);
}

export const assembleTableLocation = (segments) => {
    return (
        <span>
            <span className="sql-generator-preview-table-location-schema">{findBySegmentType(segments, SegmentType.SCHEMA_NAME).value}</span>
            <span>.</span>
            <span className="sql-generator-preview-table-location-table">{findBySegmentType(segments, SegmentType.TABLE_NAME).value}</span>
        </span>
    );
}

export const getChildrenOf = (id, segments) => {
    return new Stream(segments)
        .filter(segment => segment.parent === id)
        .toList();
}

export const findById = (id, segments) => {
    return new Stream(segments)
        .filter(segment => segment.id === id)
        .findFirst()
        .orElseThrow("IllegalArgument", "Segment not found by id " + id);
}