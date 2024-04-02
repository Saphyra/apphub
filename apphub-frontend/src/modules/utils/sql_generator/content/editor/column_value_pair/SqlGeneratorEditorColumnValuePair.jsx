import React from "react";
import localizationData from "./sql_generator_editor_column_value_pair_localization.json";
import LocalizationHandler from "../../../../../../common/js/LocalizationHandler";
import Utils from "../../../../../../common/js/Utils";
import InputField from "../../../../../../common/component/input/InputField";

const SqlGeneratorEditorColumnValuePair = ({ nameSegment, valueSegment, segments, setSegments }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const updateValue = (segment, value) => {
        segment.value = value;
        Utils.copyAndSet(segments, setSegments);
    }

    return (
        <div className="sql-generator-editor-column-value-pair">
            <InputField
                className="sql-generator-editor-column-value-pair-name"
                value={nameSegment.value}
                placeholder={localizationHandler.get("column-name")}
                onchangeCallback={(newColumnName) => updateValue(nameSegment, newColumnName)}
            />
            <span>=</span>
            <InputField
                className="sql-generator-editor-column-value-pair-value"
                value={valueSegment.value}
                placeholder={localizationHandler.get("column-value")}
                onchangeCallback={(newColumnValue) => updateValue(valueSegment, newColumnValue)}
            />
        </div>
    );
}

export default SqlGeneratorEditorColumnValuePair;