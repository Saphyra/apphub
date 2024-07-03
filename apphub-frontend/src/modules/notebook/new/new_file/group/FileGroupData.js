import Utils from "../../../../../common/js/Utils";

const FileGroupData = class {
    constructor() {
        this.id = Utils.generateRandomId();
        this.files = [];
    }
}

export default FileGroupData;