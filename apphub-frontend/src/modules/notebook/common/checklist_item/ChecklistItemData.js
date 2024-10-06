import { generateRandomId, throwException } from "../../../../common/js/Utils";
import ItemType from "../ItemType";

const ChecklistItemData = class {
    constructor(index, content = "", checked = false, checklistItemId = generateRandomId(), type = ItemType.NEW) {
        if (index === null || index === undefined) {
            throwException("IllegalArgument", "index must be set. Currently it is " + index);
        }

        this.checklistItemId = checklistItemId;
        this.index = index;
        this.content = content;
        this.checked = checked;
        this.type = type;
    }
}

export default ChecklistItemData;