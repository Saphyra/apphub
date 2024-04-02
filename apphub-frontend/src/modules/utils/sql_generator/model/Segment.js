import Utils from "../../../../common/js/Utils";

const Segment = class {
    constructor(segmentType, parent = null, order = 0, value = null, id = Utils.generateRandomId()) {
        this.segmentType = segmentType;
        this.order = order;
        this.value = value;
        this.parent = parent;
        this.id = id;
    }
}

export default Segment;