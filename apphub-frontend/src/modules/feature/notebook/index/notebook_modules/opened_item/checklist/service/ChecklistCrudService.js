import ConfirmationDialogData from "common/component/confirmation_dialog/ConfirmationDialogData";
import Button from "common/component/input/Button";
import Stream from "common/js/collection/Stream";
import { copyAndSet, throwException } from "common/js/Utils";
import UpdateType from "modules/feature/notebook/common/checklist_item/UpdateType";
import MoveDirection from "modules/feature/notebook/common/MoveDirection";
import { NOTEBOOK_DELETE_CHECKLIST_ITEM, NOTEBOOK_UPDATE_CHECKLIST_ITEM_CONTENT, NOTEBOOK_UPDATE_CHECKLIST_ITEM_STATUS } from "modules/feature/notebook/NotebookEndpoints";

export const updateItem = (listItemId, item, updateType, editingEnabled, items, setItems, setDisplaySpinner) => {
    if (!editingEnabled) {
        switch (updateType) {
            case UpdateType.TOGGLE_CHECKED:
                return NOTEBOOK_UPDATE_CHECKLIST_ITEM_STATUS.createRequest({ value: item.checked }, { listItemId: listItemId, checklistItemId: item.checklistItemId })
                    .send(setDisplaySpinner);
            case UpdateType.CONTENT_MODIFIED:
                return NOTEBOOK_UPDATE_CHECKLIST_ITEM_CONTENT.createRequest({ value: item.content }, { listItemId: listItemId, checklistItemId: item.checklistItemId })
                    .send(setDisplaySpinner);
            default:
                throwException("IllegalArgument", "Unsupported updateType: " + updateType);
        }
    }

    copyAndSet(items, setItems);
}

export const removeItem = async (listItemId, item, items, setItems, editingEnabled, setConfirmationDialogData, localizationHandler, setDisplaySpinner) => {
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
                        await NOTEBOOK_DELETE_CHECKLIST_ITEM.createRequest(null, { listItemId: listItemId, checklistItemId: item.checklistItemId })
                            .send(setDisplaySpinner);
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

export const moveItem = (item, moveDirection, items, setItems) => {
    const orderedItems = new Stream(items)
        .sorted((a, b) => a.index - b.index)
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
            throwException("IllegalArgument", "Unknown MoveDirection: " + moveDirection);
    }

    const otherItem = orderedItems[newIndex];

    if (!otherItem) {
        //Item is at the top/bottom of the list and cannot be moved further.
        return;
    }

    const originalOrder = item.index;
    const newOrder = otherItem.index;

    item.index = newOrder;
    otherItem.index = originalOrder;

    copyAndSet(items, setItems);
}