import Stream from "../../../../../../../common/js/collection/Stream";
import ChecklistItemData from "../../../../../common/checklist_item/ChecklistItemData";

const addItemToEdge = (indexRange, items, editingEnabled, setItems, setNewItemIndex) => {
    console.log(indexRange);

    const index = indexRange(items);

    if (editingEnabled) {
        const newRow = new ChecklistItemData(index);

        const copy = new Stream(items)
            .add(newRow)
            .toList();
        setItems(copy);
    } else {
        setNewItemIndex(index);
    }
}

export default addItemToEdge;