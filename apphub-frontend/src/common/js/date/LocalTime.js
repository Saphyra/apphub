const parse = (inputString) => {
    const date = new Date(0);

    const parsed = inputString.split(":");

    const hours = Number(parsed[0]);
    const minutes = Number(parsed[1]);
    const seconds = parsed.length > 2 ? Number(parsed[2]) : 0;
    const millis = parsed.length > 3 ? Number(parsed[3]) : 0;

    date.setHours(hours, minutes, seconds, millis);

    return new LocalTimeObj(date);
}

const of = (hours = 0, minutes = 0, seconds = 0, millis = 0) => {
    const date = new Date(0);

    date.setHours(hours, minutes, seconds, millis);

    return new LocalTimeObj(date);
}

const now = () => {
    const date = new Date();

    return new LocalTimeObj(date);
}

const LocalTimeObj = class {
    constructor(date) {
        this.date = date;
    }

    formatWithoutSeconds() {
        return String(this.date.getHours()).padStart(2, "0") + ":" + String(this.date.getMinutes()).padStart(2, "0");
    }
}

const LocalTime = {
    parse: parse,
    of: of,
    now: now,
}

export default LocalTime;