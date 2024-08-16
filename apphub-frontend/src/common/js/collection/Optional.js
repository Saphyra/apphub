import Utils from "../Utils";

const Optional = class {
    constructor(value) {
        this.value = value;
    }

    filter(predicate)  {
        if(this.isPresent()){
            return predicate(this.value) ? this : new Optional();
        }

        return new Optional();
    }

    get() {
        return this.value;
    }

    ifPresent(consumer) {
        if (this.isPresent()) {
            consumer(this.value);
        }
    }

    isPresent() {
        return Utils.hasValue(this.value);
    }

    map(mapper) {
        if (!this.isPresent()) {
            return this;
        }

        return new Optional(mapper(this.value));
    }

    orElse(another) {
        if (!this.isPresent()) {
            return another;
        }

        return this.value;
    }

    orElseGet(supplier) {
        if (!this.isPresent()) {
            return supplier();
        }

        return this.value;
    }

    orElseThrow(exceptionType, exceptionMessage) {
        if (!this.isPresent()) {
            Utils.throwException(exceptionType, exceptionMessage);
        }

        return this.value;
    }

    peek(consumer) {
        if (this.value) {
            consumer(this.value);
        }

        return this;
    }
}

export default Optional;