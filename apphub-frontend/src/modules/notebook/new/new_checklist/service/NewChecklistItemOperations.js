import Utils from "../../../../../common/js/Utils";
import Stream from "../../../../../common/js/collection/Stream";
import MoveDirection from "../../../common/MoveDirection";
import ChecklistItemData from "../../../common/checklist_item/ChecklistItemData";

export const addItem = (items, setItems) => {
    const maxOrder = new Stream(items)
        .map(item => item.index)
        .max()
        .orElse(0);

    const newRow = new ChecklistItemData(maxOrder + 1);
    const copy = new Stream(items)
        .add(newRow)
        .toList();

    setItems(copy);
}

export const removeItem = (item, items, setItems) => {
    const copy = new Stream(items)
        .remove(i => i === item)
        .toList();
    setItems(copy);
}

export const moveItem = (item, moveDirection, items, updateItem) => {
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
            Utils.throwException("IllegalArgument", "Unhandled MoveDirection: " + moveDirection);
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

    updateItem();
}