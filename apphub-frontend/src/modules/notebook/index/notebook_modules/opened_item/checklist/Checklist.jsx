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
import ConfirmationDialog from "../../../../../../common/component/ConfirmationDialog";

const Checklist = ({ localizationHandler, openedListItem, setOpenedListItem, setLastEvent }) => {
    const [editingEnabled, setEditingEnabled] = useState(false);
    const [parent, setParent] = useState(null);
    const [title, setTitle] = useState("");
    const [items, setItems] = useState([]);
    const [displayDiscardConfirmationDialog, setDisplayDiscardConfirmationDialog] = useState(false);

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
        setDisplayDiscardConfirmationDialog(true);
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
                id="notebook-content-checklist-title"
                placeholder={localizationHandler.get("list-item-title")}
                value={title}
                setListItemTitle={setTitle}
                disabled={!editingEnabled}
                closeButton={
                    <Button
                        id="notebook-content-checklist-close-button"
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
                        id="notebook-content-checklist-edit-button"
                        label={localizationHandler.get("edit")}
                        onclick={() => setEditingEnabled(true)}
                    />
                }

                {!editingEnabled &&
                    <Button
                        id="notebook-content-checklist-delete-checked-button"
                        label={localizationHandler.get("delete-checked")}
                        onclick={() => deleteChecked()}
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

            {displayDiscardConfirmationDialog &&
                <ConfirmationDialog
                    id="notebook-content-checklist-discard-confirmation"
                    title={localizationHandler.get("confirm-discard-title")}
                    content={localizationHandler.get("confirm-discard-content")}
                    choices={[
                        <Button
                            key="discard"
                            id="notebook-content-checklist-discard-confirm-button"
                            label={localizationHandler.get("discard")}
                            onclick={() => {
                                setEditingEnabled(false);
                                loadChecklist();
                                setDisplayDiscardConfirmationDialog(false);
                            }}
                        />,
                        <Button
                            key="cancel"
                            id="notebook-content-checklist-discard-cancel-button"
                            label={localizationHandler.get("cancel")}
                            onclick={() => setDisplayDiscardConfirmationDialog(false)}
                        />
                    ]}
                />
            }
        </div>
    );
}

export default Checklist;