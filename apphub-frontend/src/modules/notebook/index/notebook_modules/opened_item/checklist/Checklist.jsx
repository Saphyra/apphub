import React, { useEffect, useState } from "react";
import ListItemTitle from "../../../../common/list_item_title/ListItemTitle";
import Button from "../../../../../../common/component/input/Button";
import ListItemType from "../../../../common/ListItemType";
import Endpoints from "../../../../../../common/js/dao/dao";
import Stream from "../../../../../../common/js/collection/Stream";
import ChecklistItem from "../../../../common/checklist_item/ChecklistItem";
import ChecklistItemData from "../../../../common/checklist_item/ChecklistItemData";
import MoveDirection from "../../../../common/checklist_item/MoveDirection";
import Utils from "../../../../../../common/js/Utils";
import "./checklist.css";
import UpdateType from "../../../../common/checklist_item/UpdateType";
import validateListItemTitle from "../../../../common/validator/ListItemTitleValidator";
import NotificationService from "../../../../../../common/js/notification/NotificationService";
import EventName from "../../../../../../common/js/event/EventName";
import Event from "../../../../../../common/js/event/Event";

const Checklist = ({ localizationHandler, openedListItem, setOpenedListItem, setLastEvent }) => {
    const [editingEnabled, setEditingEnabled] = useState(false);
    const [parent, setParent] = useState(null);
    const [title, setTitle] = useState("");
    const [items, setItems] = useState([]);

    useEffect(() => loadChecklist(), []);

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
                    key={item.order}
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
        const newRow = new ChecklistItemData(items.length);
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

    const removeItem = (item) => {
        if (!editingEnabled) {
            Endpoints.NOTEBOOK_DELETE_CHECKLIST_ITEM.createRequest(null, { checklistItemId: item.checklistItemId })
                .send();
        }
        const copy = new Stream(items)
            .remove(i => i === item)
            .toList();
        setItems(copy);
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
        setEditingEnabled(false);
        loadChecklist();
    }

    const save = async () => {
        const result = validateListItemTitle(title);
        if (!result.valid) {
            NotificationService.showError(result.message);
            return;
        }

        const payload = {
            title: title,
            nodes: items
        }

        const response = await Endpoints.NOTEBOOK_EDIT_CHECKLIST.createRequest(payload, { listItemId: openedListItem.id })
            .send();

        setEditingEnabled(false);
        setLastEvent(new Event(EventName.NOTEBOOK_LIST_ITEM_MODIFIED));
        setDataFromResponse(response);
    }

    const deleteChecked = async () => {
        const response = await Endpoints.NOTEBOOK_CHECKLIST_DELETE_CHECKED.createRequest(null, { listItemId: openedListItem.id })
            .send();

        setDataFromResponse(response);
    }

    const orderItems = async () => {
        const response = await Endpoints.NOTEBOOK_ORDER_CHECKLIST_ITEMS.createRequest(null, { listItemId: openedListItem.id })
            .send();

        setDataFromResponse(response);
    }

    return (
        <div id="notebook-content-checklist" className="notebook-content notebook-content-view">
            <ListItemTitle
                id="notebook-content-text-title"
                placeholder={localizationHandler.get("list-item-title")}
                value={title}
                setListItemTitle={setTitle}
                disabled={!editingEnabled}
                closeButton={
                    <Button
                        id="notebook-content-text-close-button"
                        className="notebook-close-button"
                        label="X"
                        onclick={() => setOpenedListItem({ id: parent, type: ListItemType.CATEGORY })}
                    />
                }
            />

            <div id="notebook-content-checklist-content" className="notebook-content-view-main">
                {getItems()}
            </div>


            <div className="notebook-content-buttons">
                {!editingEnabled &&
                    <Button
                        id="notebook-content-text-edit-button"
                        label={localizationHandler.get("edit")}
                        onclick={() => setEditingEnabled(true)}
                    />
                }

                {!editingEnabled &&
                    <Button
                        id="notebook-content-text-delete-checked-button"
                        label={localizationHandler.get("delete-checked")}
                        onclick={() => deleteChecked()}
                    />
                }

                {!editingEnabled &&
                    <Button
                        id="notebook-content-text-order-items-button"
                        label={localizationHandler.get("order-items")}
                        onclick={() => orderItems()}
                    />
                }

                {editingEnabled &&
                    <Button
                        id="notebook-content-text-discard-button"
                        label={localizationHandler.get("discard")}
                        onclick={() => discard()}
                    />
                }

                {editingEnabled &&
                    <Button
                        id="notebook-content-text-save-button"
                        label={localizationHandler.get("save")}
                        onclick={() => save()}
                    />
                }

                {editingEnabled &&
                    <Button
                        id="notebook-content-text-add-item-button"
                        label={localizationHandler.get("add-item")}
                        onclick={() => addItem()}
                    />
                }
            </div>
        </div>
    );
}

export default Checklist;