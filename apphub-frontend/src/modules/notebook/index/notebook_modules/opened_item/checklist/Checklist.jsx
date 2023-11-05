import React, { useEffect, useState } from "react";
import Button from "../../../../../../common/component/input/Button";
import "./checklist.css";
import useHasFocus from "../../../../../../common/js/UseHasFocus";
import { useUpdateEffect } from "react-use";
import OpenedListItemHeader from "../OpenedListItemHeader";
import { confirmDeleteChcecked, loadChecklist, orderItems, save } from "./service/ChecklistDao";
import getItems from "./service/ChecklistAssembler";
import { addItem } from "./service/ChecklistCrudService";
import { close, discard } from "./service/ChecklistUtils";

const Checklist = ({ localizationHandler, openedListItem, setOpenedListItem, setLastEvent, setConfirmationDialogData }) => {
    const [editingEnabled, setEditingEnabled] = useState(false);
    const [parent, setParent] = useState(null);
    const [title, setTitle] = useState("");
    const [items, setItems] = useState([]);

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
                {getItems(items, localizationHandler, editingEnabled, setItems, setConfirmationDialogData)}
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

                {editingEnabled &&
                    <Button
                        id="notebook-content-checklist-add-item-button"
                        label={localizationHandler.get("add-item")}
                        onclick={() => addItem(items, setItems)}
                    />
                }
            </div>
        </div>
    );
}

export default Checklist;