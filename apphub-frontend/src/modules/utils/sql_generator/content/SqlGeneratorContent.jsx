import React from "react";
import "./sql_generator_content.css";
import SqlGeneratorQueryTypeSelector from "./query_type_selector/SqlGeneratorQueryTypeSelector";
import SqlGeneratorLabelEditor from "./label_editor/SqlGeneratorLabelEditor";
import SqlGeneratorPreview from "./preview/SqlGeneratorPreview";
import SqlGeneratorEditor from "./editor/SqlGeneratorEditor";
import { getChildrenOf } from "./util/SegmentUtil";

const SqlGeneratorContent = ({
    queryType,
    setQueryType,
    label,
    setLabel,
    segments,
    setSegments
}) => {
    const rootChildren = getChildrenOf(null, segments);

    return (
        <div id="sql-generator-content">
            <SqlGeneratorQueryTypeSelector
                queryType={queryType}
                setQueryType={setQueryType}
            />

            <SqlGeneratorLabelEditor
                label={label}
                setLabel={setLabel}
            />

            <div id="sql-generator-editor">
                <SqlGeneratorEditor
                    children={rootChildren}
                    segments={segments}
                    setSegments={setSegments}
                />
            </div>


            <SqlGeneratorPreview
                queryType={queryType}
                segments={segments}
            />
        </div>
    )
}

export default SqlGeneratorContent;