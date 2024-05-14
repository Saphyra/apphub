import Utils from "../../../../../common/js/Utils";

const AcquiredItemData = class {
    constructor() {
        this.id = Utils.generateRandomId();
        this.stockCategoryId = "";
        this.stockItemId = "";
        this.inCar = 0;
        this.inStorage = 0;
        this.price = 0;
        this.barCode = "";
    }
}

export default AcquiredItemData;