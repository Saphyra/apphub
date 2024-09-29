import { generateRandomId } from "../../../../../common/js/Utils";

const ImageGroupData = class {
    constructor() {
        this.id = generateRandomId();
        this.files = [];
    }
}

export default ImageGroupData;