import ConfirmationDialogData from "../../../../../../../common/component/confirmation_dialog/ConfirmationDialogData";
import Button from "../../../../../../../common/component/input/Button";
import Utils from "../../../../../../../common/js/Utils";
import Stream from "../../../../../../../common/js/collection/Stream";
import Endpoints from "../../../../../../../common/js/dao/dao";
import MoveDirection from "../../../../../common/MoveDirection";
import UpdateType from "../../../../../common/checklist_item/UpdateType";

export const updateItem = (item, updateType, editingEnabled, items, setItems) => {
    if (!editingEnabled) {
        switch (updateType) {
            case UpdateType.TOGGLE_CHECKED:
                Endpoints.NOTEBOOK_UPDATE_CHECKLIST_ITEM_STATUS.createRequest({ value: item.checked }, { checklistItemId: item.checklistItemId })
                    .send();
                break;
            case UpdateType.CONTENT_MODIFIED:
                Endpoints.NOTEBOOK_UPDATE_CHECKLIST_ITEM_CONTENT.createRequest({ value: item.content }, { checklistItemId: item.checklistItemId })
                    .send();
                break;
            default:
                Utils.throwException("IllegalArgument", "Unsupported updateType: " + updateType);
        }
    }

    Utils.copyAndSet(items, setItems);
}

export const removeItem = async (item, items, setItems, editingEnabled, setConfirmationDialogData, localizationHandler) => {
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
            Utils.throwException("IllegalArgument", "Unknown MoveDirection: " + moveDirection);
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

    Utils.copyAndSet(items, setItems);
}