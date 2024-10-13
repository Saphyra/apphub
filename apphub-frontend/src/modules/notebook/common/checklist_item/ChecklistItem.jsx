import React, { useState } from "react";
import InputField from "../../../../common/component/input/InputField";
import Button from "../../../../common/component/input/Button";
import "./checklist_item.css";
import UpdateType from "./UpdateType";
import MoveDirection from "../MoveDirection";
import ConfirmationDialog from "../../../../common/component/confirmation_dialog/ConfirmationDialog";

const ChecklistItem = ({ localizationHandler, item, updateItem, removeItem, moveItem, editingEnabled = true , tabIndex}) => {
    const [contentEditingEnabled, setContentEditingEnabled] = useState(false);
    const [modifiedContent, setModifiedContent] = useState(item.content);

    const toggleChecked = () => {
        if (window.getSelection().toString().length === 0) {
            item.checked = !item.checked;
            updateItem(item, UpdateType.TOGGLE_CHECKED);
        }
    }

    const updateChecked = (checked) => {
        item.checked = checked;
        updateItem(item, UpdateType.TOGGLE_CHECKED);
    }

    const updateContent = (content) => {
        item.content = content;
        updateItem(item, UpdateType.CONTENT_MODIFIED);
    }

    const remove = () => {
        removeItem(item);
    }

    const moveUp = () => {
        moveItem(item, MoveDirection.UP);
    }

    const moveDown = () => {
        moveItem(item, MoveDirection.DOWN);
    }

    const saveIfEnter = (e) => {
        if (e.which === 13) {
            updateContent(modifiedContent);
            setContentEditingEnabled(false);
        }
    }

    return (
        <div className={"notebook-checklist-item" + (item.checked ? " checked" : "") + (editingEnabled ? " editable" : "")}>
            <div className="notebook-checklist-item-checked-wrapper">
                <InputField
                    className="notebook-checklist-item-checked"
                    type="checkbox"
                    onchangeCallback={updateChecked}
                    checked={item.checked}
                />
            </div>

            <div className="notebook-checklist-item-content-wrapper">
                {editingEnabled ?
                    <InputField
                        className="notebook-checklist-item-content"
                        type="text"
                        placeholder={localizationHandler.get("content")}
                        onchangeCallback={updateContent}
                        value={item.content}
                        disabled={!editingEnabled}
                        tabIndex={tabIndex}
                    />
                    :
                    <div
                        className={"notebook-checklist-item-content-not-editable content-selectable" + (item.checked ? " checked" : "")}
                        onClick={toggleChecked}
                    >
                        {item.content}
                    </div>
                }

            </div>

            <div className="notebook-checklist-item-buttons">
                {editingEnabled &&
                    <Button
                        className={"notebook-checklist-item-move-up-button"}
                        label="^"
                        onclick={moveUp}
                    />
                }

                {editingEnabled &&
                    <Button
                        className={"notebook-checklist-item-move-down-button"}
                        label="v"
                        onclick={moveDown}
                    />
                }

                {!editingEnabled &&

                    <Button
                        className="notebook-checklist-item-edit-button"
                        onclick={() => setContentEditingEnabled(true)}
                    />
                }

                <Button
                    className={"notebook-checklist-item-remove-button"}
                    label="X"
                    onclick={remove}
                />
            </div>

            {contentEditingEnabled &&
                <ConfirmationDialog
                    id="notebook-checklist-item-content-edit-dialog"
                    title={localizationHandler.get("edit-checklist-item-title")}
                    content={<InputField
                        id="notebook-checklist-item-edit-input"
                        type="text"
                        placeholder={localizationHandler.get("content")}
                        value={modifiedContent}
                        onchangeCallback={setModifiedContent}
                        style={{ width: 8 * modifiedContent.length + "px" }}
                        onkeyupCallback={saveIfEnter}
                    />}
                    choices={[
                        <Button
                            key="save"
                            id="notebook-checklist-item-content-edit-save-button"
                            label={localizationHandler.get("save")}
                            onclick={() => {
                                updateContent(modifiedContent);
                                setContentEditingEnabled(false);
                            }}
                        />,
                        <Button
                            key="cancel"
                            id="notebook-checklist-item-content-edit-cancel-button"
                            label={localizationHandler.get("cancel")}
                            onclick={() => setContentEditingEnabled(false)}
                        />
                    ]}
                />
            }
        </div>
    );
}

export default ChecklistItem;