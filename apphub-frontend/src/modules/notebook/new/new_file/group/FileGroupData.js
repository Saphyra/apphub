import { generateRandomId } from "../../../../../common/js/Utils";

const FileGroupData = class {
    constructor() {
        this.id = generateRandomId();
        this.files = [];
    }
}

export default FileGroupData;