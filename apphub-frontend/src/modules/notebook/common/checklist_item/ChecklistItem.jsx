import React from "react";
import InputField from "../../../../common/component/input/InputField";
import Button from "../../../../common/component/input/Button";
import "./checklist_item.css";
import UpdateType from "./UpdateType";
import MoveDirection from "../MoveDirection";

const ChecklistItem = ({ localizationHandler, item, updateItem, removeItem, moveItem, editingEnabled = true }) => {
    const toggleChecked = () => {
        item.checked = !item.checked;
        updateItem(item, UpdateType.TOGGLE_CHECKED);
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

    return (
        <div className={"notebook-checklist-item" + (item.checked ? " checked" : "")}>
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
                    />
                    :
                    <div
                        className={"notebook-checklist-item-content-not-editable" + (item.checked ? " checked" : "")}
                        onClick={toggleChecked}
                    >
                        {item.content}
                    </div>
                }

            </div>

            <div className="notebook-checklist-item-buttons">
                <Button
                    className={"notebook-checklist-item-move-up-button"}
                    label="^"
                    onclick={moveUp}
                    disabled={!editingEnabled}
                />

                <Button
                    className={"notebook-checklist-item-move-down-button"}
                    label="v"
                    onclick={moveDown}
                    disabled={!editingEnabled}
                />

                <Button
                    className={"notebook-checklist-item-remove-button"}
                    label="X"
                    onclick={remove}
                />
            </div>
        </div>
    );
}

export default ChecklistItem;