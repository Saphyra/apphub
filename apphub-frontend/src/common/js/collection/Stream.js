import { hasValue, throwException } from "../Utils";
import MapStream from "./MapStream";
import Optional from "./Optional";

const Stream = class {
    constructor(items) {
        this.items = hasValue(items) ? items.slice() : [];
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

    allMatch(predicate) {
        return this.items.every(predicate);
    }

    anyMatch(predicate) {
        return this.items.some(predicate);
    }

    count() {
        return this.items.length;
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
        return this.items.join(delimiter);
    }

    joinToArray(delimiter) {
        if (this.items.length <= 1) {
            return this.items;
        };

        const d = typeof delimiter === "function" ? delimiter : () => delimiter;

        const r = this.items.flatMap((item, index) =>
            index === 0 ? [item] : [d(), item]
        );

        return r;
    }

    last() {
        if (!this.items.length) {
            return new Optional();
        }

        return new Optional(this.items[this.items.length - 1]);
    }

    limit(l) {
        return new Stream(this.items.slice(0, l));
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
                throwException("IllegalArgument", itemNumber + " is not a number. It is " + typeof itemNumber);
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
                throwException("IllegalArgument", item + " is not a number. It is " + typeof item);
            }

            if (item < currentMin) {
                currentMin = item;
            }
        });

        return new Optional(currentMin);
    }

    noneMatch(predicate) {
        return !this.anyMatch(predicate);
    }

    peek(consumer) {
        this.forEach(consumer);

        return this;
    }

    remove(predicate) {
        return this.filter(item => !predicate(item));
    }

    reverse() {
        this.items.reverse();
        return this;
    }

    sorted(comparator) {
        const arr = this.items.slice();

        arr.sort(comparator);

        return new Stream(arr);
    }

    sum() {
        let result = 0;
        this.forEach(item => {
            if (typeof item !== "number") {
                throwException("IllegalArgument", item + " is not a number. It is " + typeof item);
            }

            result += item;
        });

        return result;
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