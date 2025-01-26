import React, { useEffect, useState } from "react";
import Button from "../../../../../../common/component/input/Button";
import "./checklist.css";
import { useUpdateEffect } from "react-use";
import OpenedListItemHeader from "../OpenedListItemHeader";
import { addItemToTheEdge, confirmDeleteChcecked, loadChecklist, orderItems, save } from "./service/ChecklistDao";
import getItems from "./service/ChecklistAssembler";
import { close, discard } from "./service/ChecklistUtils";
import IndexRange from "../../../../common/checklist_item/IndexRange";
import addItemToEdge from "./service/ChecklistAddItemToEdgeService";
import InputField from "../../../../../../common/component/input/InputField";
import ConfirmationDialog from "../../../../../../common/component/confirmation_dialog/ConfirmationDialog";
import useHasFocus from "../../../../../../common/hook/UseHasFocus";
import { hasValue } from "../../../../../../common/js/Utils";

const Checklist = ({ localizationHandler, openedListItem, setOpenedListItem, setLastEvent, setConfirmationDialogData }) => {
    const [editingEnabled, setEditingEnabled] = useState(false);
    const [parent, setParent] = useState(null);
    const [title, setTitle] = useState("");
    const [items, setItems] = useState([]);
    const [newItemIndex, setNewItemIndex] = useState(null);
    const [newItemContent, setNewItemContent] = useState("");
    const [lastIndexRange, setLastIndexRange] = useState(null);

    useEffect(() => loadChecklist(openedListItem.id, setDataFromResponse), [openedListItem]);

    const isInFocus = useHasFocus();
    useUpdateEffect(() => {
        if (isInFocus && !editingEnabled) {
            loadChecklist(openedListItem.id, setDataFromResponse);
        }
    }, [isInFocus]);


    const setDataFromResponse = (response) => {
        setTitle(response.title);
        setItems(response.items);
        setParent(response.parent);
    }

    const addButton = (indexRange, id) => {
        return (
            <Button
                id={id}
                className="notebook-content-checklist-add-button"
                label="+"
                onclick={() => {
                    setLastIndexRange(() => indexRange);
                    addItemToEdge(indexRange, items, editingEnabled, setItems, setNewItemIndex);
                }}
            />
        );
    }

    const addItemIfEnter = (e) => {
        if (e.which === 27) {
            setNewItemContent("");
            setNewItemIndex(null);
        } else if (e.which === 13) {
            addItem(false);
        }
    }

    const addItem = (closeDialog = true) => {
        addItemToTheEdge(openedListItem.id, newItemIndex, newItemContent, setDataFromResponse);
        addItemToEdge(lastIndexRange, items, editingEnabled, setItems, setNewItemIndex);
        setNewItemContent("");

        if (closeDialog) {
            setNewItemIndex(null);
        }
    }

    const getNewItemConfirmationDialog = () => {
        return (
            <ConfirmationDialog
                id="notebook-add-checklist-item-to-the-edge-dialog"
                title={localizationHandler.get("add-checklist-item-title")}
                content={<InputField
                    id="notebook-add-checklist-item-to-the-edge-input"
                    type="text"
                    placeholder={localizationHandler.get("content")}
                    value={newItemContent}
                    onchangeCallback={setNewItemContent}
                    style={{ width: 8 * newItemContent.length + "px" }}
                    onkeyupCallback={addItemIfEnter}
                />}
                choices={[
                    <Button
                        key="save"
                        id="notebook-add-checklist-item-to-the-edge-save-button"
                        label={localizationHandler.get("save")}
                        onclick={addItem}
                    />,
                    <Button
                        key="cancel"
                        id="notebook-add-checklist-item-to-the-edge-cancel-button"
                        label={localizationHandler.get("cancel")}
                        onclick={() => setNewItemIndex(null)}
                    />
                ]}
            />
        )
    }

    return (
        <div id="notebook-content-checklist" className={"notebook-content notebook-content-view" + (editingEnabled ? " editable" : "")}>
            <OpenedListItemHeader
                localizationHandler={localizationHandler}
                title={title}
                setTitle={setTitle}
                editingEnabled={editingEnabled}
                close={() => close(editingEnabled, setConfirmationDialogData, localizationHandler, setOpenedListItem, parent)}
            />

            <div id="notebook-content-checklist-content" className="notebook-content-view-main">
                {addButton(IndexRange.MIN, "notebook-content-checklist-add-item-to-start")}

                <div>
                    {getItems(items, localizationHandler, editingEnabled, setItems, setConfirmationDialogData)}
                </div>

                {addButton(IndexRange.MAX, "notebook-content-checklist-add-item-to-end")}
            </div>


            <div className="notebook-content-buttons">
                {!editingEnabled &&
                    <Button
                        id="notebook-content-checklist-edit-button"
                        label={localizationHandler.get("edit")}
                        onclick={() => setEditingEnabled(true)}
                    />
                }

                {!editingEnabled &&
                    <Button
                        id="notebook-content-checklist-delete-checked-button"
                        label={localizationHandler.get("delete-checked")}
                        onclick={() => confirmDeleteChcecked(setConfirmationDialogData, localizationHandler, openedListItem, setDataFromResponse)}
                    />
                }

                {!editingEnabled &&
                    <Button
                        id="notebook-content-checklist-order-items-button"
                        label={localizationHandler.get("order-items")}
                        onclick={() => orderItems(openedListItem.id, setDataFromResponse)}
                    />
                }

                {editingEnabled &&
                    <Button
                        id="notebook-content-checklist-discard-button"
                        label={localizationHandler.get("discard")}
                        onclick={() => discard(setConfirmationDialogData, localizationHandler, setEditingEnabled, openedListItem.id, setDataFromResponse)}
                    />
                }

                {editingEnabled &&
                    <Button
                        id="notebook-content-checklist-save-button"
                        label={localizationHandler.get("save")}
                        onclick={() => save(title, items, openedListItem, setEditingEnabled, setLastEvent, setDataFromResponse)}
                    />
                }
            </div>

            {hasValue(newItemIndex) && getNewItemConfirmationDialog()}
        </div>
    );
}

export default Checklist;