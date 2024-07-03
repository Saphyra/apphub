import Utils from "../../../../../common/js/Utils";

const ImageGroupData = class {
    constructor() {
        this.id = Utils.generateRandomId();
        this.files = [];
    }
}

export default ImageGroupData;