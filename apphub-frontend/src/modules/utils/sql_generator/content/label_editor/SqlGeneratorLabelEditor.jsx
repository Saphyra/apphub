import React from "react";
import InputField from "../../../../../common/component/input/InputField";
import "./sql_generator_label_editor.css";
import localizationData from "./sql_generator_label_editor_localization.json";
import LocalizationHandler from "../../../../../common/js/LocalizationHandler";

const SqlGeneratorLabelEditor = ({ label, setLabel }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    return (
        <div id="sql-generator-label-editor">
            <InputField
                id="sql-generator-label-editor-input"
                placeholder={localizationHandler.get("label")}
                value={label}
                onchangeCallback={setLabel}
            />
        </div>
    );
}

export default SqlGeneratorLabelEditor;