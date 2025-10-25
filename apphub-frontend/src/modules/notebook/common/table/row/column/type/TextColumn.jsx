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
                <div className="notebook-table-column-wrapper">
                    <div className="notebook-table-column-content">
                        <Textarea
                            className={"notebook-table-column-input resizable" + (custom ? " notebook-table-column-input" : "")}
                            onchangeCallback={updateContent}
                            value={columnData.data}
                            onKeyDownCallback={e => {
                                if (e.key === "Tab") {
                                    e.preventDefault();

                                    const start = e.target.selectionStart;
                                    const end = e.target.selectionEnd;
                                    e.target.value = e.target.value.substring(0, start) + "    " + e.target.value.substring(end);
                                    e.target.selectionStart = e.target.selectionEnd = start + 4;
                                }
                            }}
                            onKeyUpCallback={e => {
                                e.target.style.height = "auto";
                                e.target.style.height = e.target.scrollHeight + 6 + "px";
                            }}
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
                <pre className="notebook-table-column-wrapper notebook-text-column">
                    {columnData.data}
                </pre>
            </td >
        );
    }
}

export default TextColumn;