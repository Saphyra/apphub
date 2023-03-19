import Optional from "./Optional";

const Stream = class {
    constructor(items) {
        this.items = items || [];
    }

    filter(predicate) {
        const result = [];

        this.items.forEach(item => {
            if (predicate(item)) {
                result.push(item);
            }
        });

        return new Stream(result);
    }

    findFirst() {
        if (this.items.length == 0) {
            return new Optional();
        }

        return new Optional(this.items[0]);
    }
}

export default Stream;