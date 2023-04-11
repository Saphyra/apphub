import MapStream from "./MapStream";
import Optional from "./Optional";

const Stream = class {
    constructor(items) {
        this.items = items === null ? [] : items.slice();
    }

    add(item) {
        this.items.push(item);

        return this;
    }

    anyMatch(predicate) {
        return this.items.some(predicate);
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
        if (this.items.length === 0) {
            return new Optional();
        }

        return new Optional(this.items[0]);
    }

    forEach(consumer) {
        this.items.forEach(consumer);
    }

    map(mapper) {
        return new Stream(this.items.map(mapper));
    }

    peek(consumer) {
        this.forEach(consumer);

        return this;
    }

    sorted(comparator) {
        const arr = this.items.slice();

        arr.sort(comparator);

        return new Stream(arr);
    }

    toList() {
        return this.items;
    }

    toMap(keyMapper, valueMapper = (item) => item) {
        const result = {};

        this.forEach(item => result[keyMapper(item)] = valueMapper(item));

        return result;
    }

    toMapStream(keyMapper, valueMapper = (item) => item) {
        return new MapStream(this.toMap(keyMapper, valueMapper));
    }
}

export default Stream;