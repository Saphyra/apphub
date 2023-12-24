import Utils from "../Utils";
import MapStream from "./MapStream";
import Optional from "./Optional";

const Stream = class {
    constructor(items) {
        this.items = Utils.hasValue(items) ? items.slice() : [];
    }

    add(item) {
        this.items.push(item);

        return new Stream(this.items);
    }

    addAll(items) {
        for (let i in items) {
            this.items.push(items[i]);
        }

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

    flatMap(mapper) {
        const result = new Stream();

        this.map(item => mapper(item))
            .forEach(items => result.addAll(items.toList()));

        return result;
    }

    forEach(consumer) {
        this.items.forEach(consumer);
    }

    groupBy(idSupplier) {
        const result = {};

        this.forEach((item) => {
            const id = idSupplier(item);
            if (!result[id]) {
                result[id] = [];
            }

            result[id].push(item);
        });

        return new MapStream(result);
    }

    join(delimiter = "") {
        return this.toList()
            .join(delimiter);
    }

    map(mapper) {
        return new Stream(this.items.map(mapper));
    }

    max(toIntFunction = (item) => item) {
        if (this.items.length === 0) {
            return new Optional(null);
        }

        let currentMax = Number.MIN_SAFE_INTEGER;

        this.forEach(item => {
            const itemNumber = toIntFunction(item);

            if (typeof itemNumber !== "number") {
                Utils.throwException("IllegalArgument", itemNumber + " is not a number. It is " + typeof itemNumber);
            }

            if (itemNumber > currentMax) {
                currentMax = itemNumber;
            }
        });

        return new Optional(currentMax);
    }

    min() {
        if (this.items.length === 0) {
            return new Optional(null);
        }

        let currentMin = Number.MAX_SAFE_INTEGER;

        this.forEach(item => {
            if (typeof item !== "number") {
                Utils.throwException("IllegalArgument", item + " is not a number. It is " + typeof item);
            }

            if (item < currentMin) {
                currentMin = item;
            }
        });

        return new Optional(currentMin);
    }

    peek(consumer) {
        this.forEach(consumer);

        return this;
    }

    remove(predicate) {
        return this.filter(item => !predicate(item));
    }

    reverse(){
        this.items.reverse();
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