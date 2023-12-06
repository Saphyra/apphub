import Stream from "../../../../../common/js/collection/Stream";
import ChecklistItem from "../../../common/checklist_item/ChecklistItem";
import { moveItem, removeItem } from "./NewChecklistItemOperations";

const updateItem = (items, setItems) => {
    const copy = new Stream(items)
        .toList();
    setItems(copy);
}

const getItems = (items, localizationHandler, setItems) => {
    if (items.length === 0) {
        return (
            <div id="notebook-new-checklist-no-items">{localizationHandler.get("no-items")}</div>
        );
    }

    return new Stream(items)
        .sorted((a, b) => a.index - b.index)
        .map(item =>
            <ChecklistItem
                key={item.index}
                localizationHandler={localizationHandler}
                item={item}
                updateItem={() => updateItem(items, setItems)}
                removeItem={(item) => removeItem(item, items, setItems)}
                moveItem={(item, moveDirection) => moveItem(item, moveDirection, items, () => updateItem(items, setItems))}
            />
        )
        .toList();
}

export default getItems;