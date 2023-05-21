import Utils from "../Utils";

const Optional = class {
    constructor(value) {
        this.value = value;
    }

    orElse(another) {
        if (this.value === null || this.value === undefined) {
            return another;
        }

        return this.value;
    }

    orElseThrow(exceptionType, exceptionMessage) {
        if (this.value === null || this.value === undefined) {
            Utils.throwException(exceptionType, exceptionMessage);
        }

        return this.value;
    }
}

export default Optional;