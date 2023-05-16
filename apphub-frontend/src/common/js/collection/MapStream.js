import Optional from "./Optional";
import Stream from "./Stream";

const MapStream = class {
    constructor(items) {
        this.items = items;
    }

    add(key, value) {
        this.items[key] = value;

        return this;
    }

    clone() {
        const result = {};

        this.forEach((key, value) => result[key] = value);

        return new MapStream(result);
    }

    filter(predicate) {
        const result = {};

        this.forEach((key, value) => {
            if (predicate(key, value)) {
                result[key] = value;
            }
        });

        return new MapStream(result);
    }

    findAny() {
        const keys = Object.keys(this.items);

        if (keys.length === 0) {
            return new Optional();
        }

        return new Optional(this.items[keys[0]]);
    }

    forEach(consumer) {
        Object.keys(this.items)
            .forEach((key) => consumer(key, this.items[key]));
    }

    map(mapper) {
        const result = {};

        Object.keys(this.items)
            .forEach((key) => result[key] = mapper(key, this.items[key]));

        return new MapStream(result);
    }

    peek(consumer){
        this.forEach(consumer);

        return this;
    }

    sorted(comparator) {
        return this.toListStream((key, value) => { return { key: key, value: value } })
            .sorted(comparator)
            .toMapStream(item => item.key, item => item.value);
    }

    toListStream(mapper){
        return new Stream(this.toList(mapper));
    }

    toList(mapper = (key, value) => value) {
        const result = [];

        this.map((key, value) => result.push(mapper(key, value)));

        return result;
    }

    toObject() {
        return this.items;
    }
}

export default MapStream;