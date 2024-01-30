import React from "react";
import InputField from "../../../../../../../common/component/input/InputField";
import Button from "../../../../../../../common/component/input/Button";
import PreLabeledInputField from "../../../../../../../common/component/input/PreLabeledInputField";

const LinkColumn = ({
    columnData,
    updateColumn,
    editingEnabled = true,
    selectType,
    localizationHandler
}) => {
    const updateLabel = (newValue) => {
        columnData.data.label = newValue;
        updateColumn();
    }

    const updateUrl = (newValue) => {
        columnData.data.url = newValue;
        updateColumn();
    }

    if (editingEnabled) {
        return (
            <td className={"table-column editable notebook-table-column-type-" + columnData.columnType.toLowerCase()}>
                <div className="table-column-wrapper">
                    <div className="table-column-content">
                        <PreLabeledInputField
                            label={localizationHandler.get("label") + ":"}
                            input={<InputField
                                className={"notebook-table-column-input notebook-custom-table-column-link-label"}
                                type="text"
                                placeholder={localizationHandler.get("label")}
                                onchangeCallback={updateLabel}
                                value={columnData.data.label}
                            />}
                        />

                        <PreLabeledInputField
                            label={localizationHandler.get("url") + ":"}
                            input={<InputField
                                className={"notebook-table-column-input notebook-custom-table-column-link-url"}
                                type="text"
                                placeholder={localizationHandler.get("url")}
                                onchangeCallback={updateUrl}
                                value={columnData.data.url}
                            />}
                        />
                    </div>

                    <Button
                        className="notebook-table-change-column-type-button"
                        onclick={selectType}
                        title={localizationHandler.get("change-column-type")}
                    />
                </div>
            </td>
        )
    } else {
        return (
            <td className="table-column">
                <div
                    className="table-column-wrapper button"
                    onClick={() => window.open(columnData.data.url)}
                >
                    {columnData.data.label}
                </div>
            </td >
        );
    }
}

export default LinkColumn;