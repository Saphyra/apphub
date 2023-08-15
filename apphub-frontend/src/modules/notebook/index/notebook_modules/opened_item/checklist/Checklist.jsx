import React, { useEffect, useState } from "react";
import ListItemTitle from "../../../../common/list_item_title/ListItemTitle";
import Button from "../../../../../../common/component/input/Button";
import ListItemType from "../../../../common/ListItemType";
import Endpoints from "../../../../../../common/js/dao/dao";
import Stream from "../../../../../../common/js/collection/Stream";
import ChecklistItem from "../../../../common/checklist_item/ChecklistItem";
import ChecklistItemData from "../../../../common/checklist_item/ChecklistItemData";
import MoveDirection from "../../../../common/MoveDirection";
import Utils from "../../../../../../common/js/Utils";
import "./checklist.css";
import UpdateType from "../../../../common/checklist_item/UpdateType";
import validateListItemTitle from "../../../../common/validator/ListItemTitleValidator";
import NotificationService from "../../../../../../common/js/notification/NotificationService";
import EventName from "../../../../../../common/js/event/EventName";
import Event from "../../../../../../common/js/event/Event";
import ConfirmationDialogData from "../../../../../../common/component/confirmation_dialog/ConfirmationDialogData";
import useHasFocus from "../../../../../../common/js/UseHasFocus";
import { useUpdateEffect } from "react-use";

const Checklist = ({ localizationHandler, openedListItem, setOpenedListItem, setLastEvent, setConfirmationDialogData }) => {
    const [editingEnabled, setEditingEnabled] = useState(false);
    const [parent, setParent] = useState(null);
    const [title, setTitle] = useState("");
    const [items, setItems] = useState([]);

    useEffect(() => loadChecklist(), [openedListItem]);

    const isInFocus = useHasFocus();
    useUpdateEffect(() => {
        if (isInFocus && !editingEnabled) {
            loadChecklist();
        }
    }, [isInFocus]);

    const loadChecklist = () => {
        const fetch = async () => {
            const response = await Endpoints.NOTEBOOK_GET_CHECKLIST.createRequest(null, { listItemId: openedListItem.id })
                .send();

            setDataFromResponse(response);
        }
        fetch();
    }

    const setDataFromResponse = (response) => {
        setTitle(response.title);
        setItems(response.nodes);
        setParent(response.parent);
    }

    const getItems = () => {
        if (items.length === 0) {
            return (
                <div id="notebook-view-checklist-empty">
                    {localizationHandler.get("checklist-empty")}
                </div>
            );
        }

        return new Stream(items)
            .sorted((a, b) => a.order - b.order)
            .map(item =>
                <ChecklistItem
                    key={item.checklistItemId}
                    localizationHandler={localizationHandler}
                    item={item}
                    updateItem={updateItem}
                    removeItem={removeItem}
                    moveItem={moveItem}
                    editingEnabled={editingEnabled}
                />
            )
            .toList();
    }

    const addItem = () => {
        const order = new Stream(items)
            .map(item => item.order)
            .max()
            .orElse(0)
            + 1

        const newRow = new ChecklistItemData(order);
        const copy = new Stream(items)
            .add(newRow)
            .toList();

        setItems(copy);
    }

    const updateItem = (item, updateType) => {
        if (!editingEnabled) {
            switch (updateType) {
                case UpdateType.TOGGLE_CHECKED:
                    Endpoints.NOTEBOOK_UPDATE_CHECKLIST_ITEM_STATUS.createRequest({ value: item.checked }, { checklistItemId: item.checklistItemId })
                        .send();
                    break;
                default:
                    Utils.throwException("IllegalArgument", "Unsupported updateType: " + updateType);
            }
        }
        const copy = new Stream(items)
            .toList();
        setItems(copy);
    }

    const removeItem = async (item) => {
        const doRemoveItem = (item) => {
            const copy = new Stream(items)
                .remove(i => i === item)
                .toList();
            setItems(copy);
        }

        if (!editingEnabled) {
            setConfirmationDialogData(new ConfirmationDialogData(
                "notebook-content-checklist-item-deletion-confirmation",
                localizationHandler.get("confirm-checklist-item-deletion-title"),
                localizationHandler.get("confirm-checklist-item-deletion-content"),
                [
                    <Button
                        key="delete"
                        id="notebook-content-checklist-item-deletion-confirm-button"
                        label={localizationHandler.get("delete")}
                        onclick={async () => {
                            await Endpoints.NOTEBOOK_DELETE_CHECKLIST_ITEM.createRequest(null, { checklistItemId: item.checklistItemId })
                                .send();
                            setConfirmationDialogData(null);
                            doRemoveItem(item);
                        }}
                    />,
                    <Button
                        key="cancel"
                        id="notebook-content-checklist-item-deletion-cancel-button"
                        label={localizationHandler.get("cancel")}
                        onclick={() => setConfirmationDialogData(null)}
                    />
                ]
            ));
        } else {
            doRemoveItem(item);
        }
    }

    const moveItem = (item, moveDirection) => {
        const orderedItems = new Stream(items)
            .sorted((a, b) => a.order - b.order)
            .toList();

        const itemIndex = orderedItems.indexOf(item);

        let newIndex;
        switch (moveDirection) {
            case MoveDirection.UP:
                newIndex = itemIndex - 1;
                break;
            case MoveDirection.DOWN:
                newIndex = itemIndex + 1;
                break;
            default:
                Utils.throwException("IllegalArgument", "Unknown MoveDirection: " + moveDirection);
        }

        const otherItem = orderedItems[newIndex];

        if (!otherItem) {
            //Item is at the top/bottom of the list and cannot be moved further.
            return;
        }

        const originalOrder = item.order;
        const newOrder = otherItem.order;

        item.order = newOrder;
        otherItem.order = originalOrder;

        updateItem();
    }


    const discard = () => {
        setConfirmationDialogData(new ConfirmationDialogData(
            "notebook-content-checklist-discard-confirmation",
            localizationHandler.get("confirm-discard-title"),
            localizationHandler.get("confirm-discard-content"),
            [
                <Button
                    key="discard"
                    id="notebook-content-checklist-discard-confirm-button"
                    label={localizationHandler.get("discard")}
                    onclick={() => {
                        setEditingEnabled(false);
                        loadChecklist();
                        setConfirmationDialogData(null);
                    }}
                />,
                <Button
                    key="cancel"
                    id="notebook-content-checklist-discard-cancel-button"
                    label={localizationHandler.get("cancel")}
                    onclick={() => setConfirmationDialogData(null)}
                />
            ]
        ));
    }

    const save = async () => {
        const result = validateListItemTitle(title);
        if (!result.valid) {
            NotificationService.showError(result.message);
            return;
        }

        const payload = {
            title: title,
            nodes: new Stream(items)
                .peek(item => {
                    if (item.new) {
                        item.checklistItemId = null;
                    }
                })
                .toList()
        }

        const response = await Endpoints.NOTEBOOK_EDIT_CHECKLIST.createRequest(payload, { listItemId: openedListItem.id })
            .send();

        setEditingEnabled(false);
        setLastEvent(new Event(EventName.NOTEBOOK_LIST_ITEM_MODIFIED));
        setDataFromResponse(response);
    }

    const confirmDeleteChcecked = () => {
        setConfirmationDialogData(new ConfirmationDialogData(
            "notebook-content-checklist-delete-checked-confirmation",
            localizationHandler.get("confirm-delete-checked-title"),
            localizationHandler.get("confirm-delete-checked-content"),
            [
                <Button
                    key="delete"
                    id="notebook-content-checklist-delete-checked-confirm-button"
                    label={localizationHandler.get("delete-checked")}
                    onclick={deleteChecked}
                />,
                <Button
                    key="cancel"
                    id="notebook-content-checklist-delete-checked-cancel-button"
                    label={localizationHandler.get("cancel")}
                    onclick={() => setConfirmationDialogData(null)}
                />
            ]
        ));
    }

    const deleteChecked = async () => {
        const response = await Endpoints.NOTEBOOK_CHECKLIST_DELETE_CHECKED.createRequest(null, { listItemId: openedListItem.id })
            .send();

        setDataFromResponse(response);
        setConfirmationDialogData(null);
    }

    const orderItems = async () => {
        const response = await Endpoints.NOTEBOOK_ORDER_CHECKLIST_ITEMS.createRequest(null, { listItemId: openedListItem.id })
            .send();

        setDataFromResponse(response);
    }

    const close = () => {
        if (editingEnabled) {
            setConfirmationDialogData(new ConfirmationDialogData(
                "notebook-content-checklist-close-confirmation",
                localizationHandler.get("confirm-close-title"),
                localizationHandler.get("confirm-close-content"),
                [
                    <Button
                        key="close"
                        id="notebook-content-checklist-close-confirm-button"
                        label={localizationHandler.get("close")}
                        onclick={doClose}
                    />,
                    <Button
                        key="cancel"
                        id="notebook-content-checklist-close-cancel-button"
                        label={localizationHandler.get("cancel")}
                        onclick={() => setConfirmationDialogData(null)}
                    />
                ]
            ));
        } else {
            doClose();
        }
    }

    const doClose = () => {
        setOpenedListItem({ id: parent, type: ListItemType.CATEGORY })
        setConfirmationDialogData(null);
    }

    return (
        <div id="notebook-content-checklist" className="notebook-content notebook-content-view">
            <ListItemTitle
                inputId="notebook-content-checklist-title"
                placeholder={localizationHandler.get("list-item-title")}
                value={title}
                setListItemTitle={setTitle}
                disabled={!editingEnabled}
                closeButton={
                    <Button
                        id="notebook-content-checklist-close-button"
                        className="notebook-close-button"
                        label="X"
                        onclick={close}
                    />
                }
            />

            <div id="notebook-content-checklist-content" className="notebook-content-view-main">
                {getItems()}
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
                        onclick={() => confirmDeleteChcecked()}
                    />
                }

                {!editingEnabled &&
                    <Button
                        id="notebook-content-checklist-order-items-button"
                        label={localizationHandler.get("order-items")}
                        onclick={() => orderItems()}
                    />
                }

                {editingEnabled &&
                    <Button
                        id="notebook-content-checklist-discard-button"
                        label={localizationHandler.get("discard")}
                        onclick={() => discard()}
                    />
                }

                {editingEnabled &&
                    <Button
                        id="notebook-content-checklist-save-button"
                        label={localizationHandler.get("save")}
                        onclick={() => save()}
                    />
                }

                {editingEnabled &&
                    <Button
                        id="notebook-content-checklist-add-item-button"
                        label={localizationHandler.get("add-item")}
                        onclick={() => addItem()}
                    />
                }
            </div>
        </div>
    );
}

export default Checklist;