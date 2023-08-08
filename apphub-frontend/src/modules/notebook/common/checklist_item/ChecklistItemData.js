import Utils from "../../../../common/js/Utils";

const ChecklistItemData = class {
    constructor(order, content = "", checked = false, checklistItemId = null) {
        if (order === null || order === undefined) {
            Utils.throwException("IllegalArgument", "order must be set. Currently it is " + order);
        }

        this.checklistItemId = checklistItemId || Utils.generateRandomId();
        this.order = order;
        this.content = content;
        this.checked = checked;
        this.new = checklistItemId == null;
    }
}

export default ChecklistItemData;