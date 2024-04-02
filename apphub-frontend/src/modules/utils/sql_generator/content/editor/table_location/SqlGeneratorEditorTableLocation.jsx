import React from "react";
import InputField from "../../../../../../common/component/input/InputField";
import localizationData from "./sql_generator_editor_table_location_localization.json";
import LocalizationHandler from "../../../../../../common/js/LocalizationHandler";
import Utils from "../../../../../../common/js/Utils";

const SqlGeneratorEditorTableLocation = ({ schemaNameSegment, tableNameSegment, segments, setSegments }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const updateValue = (segment, value) => {
        segment.value = value;
        Utils.copyAndSet(segments, setSegments);
    }

    return (
        <div className="sql-generator-editor-table-location">
            <InputField
                className="sql-generator-editor-table-location-schema"
                value={schemaNameSegment.value}
                placeholder={localizationHandler.get("schema-name")}
                onchangeCallback={(newSchemaName) => updateValue(schemaNameSegment, newSchemaName)}
            />
            <span>/</span>
            <InputField
                className="sql-generator-editor-table-location-table"
                value={tableNameSegment.value}
                placeholder={localizationHandler.get("table-name")}
                onchangeCallback={(newTableName) => updateValue(tableNameSegment, newTableName)}
            />
        </div>
    );
}

export default SqlGeneratorEditorTableLocation;