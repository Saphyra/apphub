import Utils from "../../../../common/js/Utils";
import ItemType from "../ItemType";

const ChecklistItemData = class {
    constructor(index, content = "", checked = false, checklistItemId = Utils.generateRandomId(), type = ItemType.NEW) {
        if (index === null || index === undefined) {
            Utils.throwException("IllegalArgument", "index must be set. Currently it is " + index);
        }

        this.checklistItemId = checklistItemId || Utils.generateRandomId();
        this.index = index;
        this.content = content;
        this.checked = checked;
        this.type = type;
    }
}

export default ChecklistItemData;