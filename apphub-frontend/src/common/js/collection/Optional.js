import Utils from "../Utils";

const Optional = class {
    constructor(value) {
        this.value = value;
    }

    map(mapper) {
        if (!Utils.hasValue(this.value)) {
            return this;
        }

        return new Optional(mapper(this.value));
    }

    orElse(another) {
        if (!Utils.hasValue(this.value)) {
            return another;
        }

        return this.value;
    }

    orElseGet(supplier) {
        if (!Utils.hasValue(this.value)) {
            return supplier();
        }

        return this.value;
    }

    orElseThrow(exceptionType, exceptionMessage) {
        if (!Utils.hasValue(this.value)) {
            Utils.throwException(exceptionType, exceptionMessage);
        }

        return this.value;
    }
}

export default Optional;