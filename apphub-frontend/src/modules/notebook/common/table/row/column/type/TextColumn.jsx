import React from "react";
import InputField from "../../../../../../../common/component/input/InputField";
import Button from "../../../../../../../common/component/input/Button";
import Textarea from "../../../../../../../common/component/input/Textarea";

const TextColumn = ({
    columnData,
    updateColumn,
    editingEnabled = true,
    custom = false,
    selectType,
    localizationHandler
}) => {
    const updateContent = (newValue) => {
        columnData.data = newValue;
        updateColumn();
    }
    if (editingEnabled) {
        return (
            <td className={"table-column editable notebook-table-column-type-" + columnData.columnType.toLowerCase()}>
                <div className="table-column-wrapper">
                    <div className="table-column-content">
                        <Textarea
                            className={"notebook-table-column-input resizable" + (custom ? " notebook-table-column-input" : "")}
                            onchangeCallback={updateContent}
                            value={columnData.data}
                        />
                    </div>

                    {custom &&
                        <Button
                            className="notebook-table-change-column-type-button"
                            onclick={selectType}
                            title={localizationHandler.get("change-column-type")}
                        />
                    }
                </div>
            </td>
        )
    } else {
        return (
            <td className="table-column">
                <pre className="table-column-wrapper">
                    {columnData.data}
                </pre>
            </td >
        );
    }
}

export default TextColumn;