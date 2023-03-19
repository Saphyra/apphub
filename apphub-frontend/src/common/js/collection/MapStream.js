import Optional from "./Optional";

const MapStream = class {
    constructor(items) {
        this.items = items;
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

    findAny(){
        const keys = Object.keys(this.items);

        if(keys.length === 0){
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

    toObject() {
        return this.items;
    }
}

export default MapStream;