import Stream from "../../../../../../../common/js/collection/Stream";
import ChecklistItem from "../../../../../common/checklist_item/ChecklistItem";
import { moveItem, removeItem, updateItem } from "./ChecklistCrudService";

const getItems = (items, searchText, localizationHandler, editingEnabled, setItems, setConfirmationDialogData) => {
    if (items.length === 0) {
        return (
            <div id="notebook-view-checklist-empty">
                {localizationHandler.get("checklist-empty")}
            </div>
        );
    }

    return new Stream(items)
        .sorted((a, b) => a.index - b.index)
        .filter(item => item.content.toLowerCase().includes(searchText.toLowerCase()))
        .map((item, tabIndex) =>
            <ChecklistItem
                key={item.checklistItemId}
                localizationHandler={localizationHandler}
                item={item}
                updateItem={(item, updateType) => updateItem(item, updateType, editingEnabled, items, setItems)}
                removeItem={(item) => removeItem(item, items, setItems, editingEnabled, setConfirmationDialogData, localizationHandler)}
                moveItem={(item, moveDirection) => moveItem(item, moveDirection, items, setItems)}
                editingEnabled={editingEnabled}
                tabIndex={tabIndex + 1}
            />
        )
        .toList();
}

export default getItems;