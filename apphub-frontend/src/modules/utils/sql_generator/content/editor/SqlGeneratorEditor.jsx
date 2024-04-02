import React from "react";
import SqlGeneratorEditorTableLocation from "./table_location/SqlGeneratorEditorTableLocation";
import Stream from "../../../../../common/js/collection/Stream";
import Utils from "../../../../../common/js/Utils";
import SegmentType from "../../model/SegmentType";
import { findBySegmentType, getChildrenOf } from "../util/SegmentUtil";
import SqlGeneratorEditorColumnValuePair from "./column_value_pair/SqlGeneratorEditorColumnValuePair";
import Button from "../../../../../common/component/input/Button";
import Segment from "../../model/Segment";

const SqlGeneratorEditor = ({ children, segments, setSegments }) => {
    const getContent = () => {
        return new Stream(children)
            .sorted((a, b) => a.order - b.order)
            .map(segment => getSegment(segment))
            .toList();
    }

    const getSegment = (segment) => {
        const ch = getChildrenOf(segment.id, segments)

        switch (segment.segmentType) {
            case SegmentType.TABLE_LOCATION:
                return <SqlGeneratorEditorTableLocation
                    key={segment.id}
                    schemaNameSegment={findBySegmentType(ch, SegmentType.SCHEMA_NAME)}
                    tableNameSegment={findBySegmentType(ch, SegmentType.TABLE_NAME)}
                    segments={segments}
                    setSegments={setSegments}
                />
            case SegmentType.COLUMN_VALUE_PAIRS:
                return (
                    <div 
                    key={segment.id}
                    className="sql-generator-editor-key-value-pairs"
                    >
                        <SqlGeneratorEditor
                            children={ch}
                            segments={segments}
                            setSegments={setSegments}
                        />

                        <Button
                            className={"sql-generator-editor-add-new-key-value-pair"}
                            label={"+"}
                            onclick={() => addKeyValuePair(segment.id)}
                        />
                    </div>
                );
            case SegmentType.COLUMN_VALUE_PAIR:
                return <SqlGeneratorEditorColumnValuePair
                    key={segment.id}
                    nameSegment={findBySegmentType(ch, SegmentType.COLUMN_NAME)}
                    valueSegment={findBySegmentType(ch, SegmentType.COLUMN_VALUE)}
                    segments={segments}
                    setSegments={setSegments}
                />
            default:
                Utils.throwException("IllegalArgument", "Cannot get segment component for segmentType " + segment.segmentType);
        }
    }

    const addKeyValuePair = (id) => {
        const index = new Stream(getChildrenOf(id, segments))
            .map(segment => segment.order)
            .max()
            .orElse(-1)
            + 1;

        console.log(index);

        const columnValuePair = new Segment(SegmentType.COLUMN_VALUE_PAIR, id, index);
        const columnName = new Segment(SegmentType.COLUMN_NAME, columnValuePair.id, 0, "");
        const columnValue = new Segment(SegmentType.COLUMN_VALUE, columnValuePair.id, 1, "");

        const copy = new Stream(segments)
            .add(columnValuePair)
            .add(columnName)
            .add(columnValue)
            .toList();
        setSegments(copy);
    }

    return (
        <div className="sql-generator-editor-item">
            {getContent()}
        </div>
    );
}

export default SqlGeneratorEditor; 