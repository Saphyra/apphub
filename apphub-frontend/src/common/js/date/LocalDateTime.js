import Utils from "../Utils";

const fromEpochSeconds = (epoch) => {
    const d = new Date(0);
    d.setUTCSeconds(epoch);
    return new LocalDateTimeObj(d);
}

const create = (date) => {
    return new LocalDateTimeObj(date);
}

const now = () => {
    return create(new Date());
}

class LocalDateTimeObj {
    constructor(date) {
        if (!Utils.hasValue(date)) {
            Utils.throwException("IllegalArgument", "date must not be null");
        }

        if (!(date instanceof Date)) {
            Utils.throwException("IllegalArgument", "Date is not a Date");
        }

        this.date = date;
    }

    getMonth() {
        return String(this.date.getMonth() + 1).padStart(2, '0'); //January is 0!
    }

    getYear() {
        return this.date.getFullYear();
    }

    getDay() {
        return String(this.date.getDate()).padStart(2, '0');
    }

    getHours() {
        return String(this.date.getHours()).padStart(2, '0');
    }

    getMinutes() {
        return String(this.date.getMinutes()).padStart(2, '0');
    }

    getSeconds() {
        return String(this.date.getSeconds()).padStart(2, '0');
    }

    getEpoch() {
        return this.date.valueOf();
    }

    format() {
        return this.getYear() + "-" + this.getMonth() + "-" + this.getDay() + " " + this.getHours() + ":" + this.getMinutes() + ":" + this.getSeconds();
    }

    equals(obj) {
        if (!Utils.hasValue(obj)) {
            return false;
        }

        if (!obj instanceof LocalDateTimeObj) {
            return false;
        }

        return obj.toString() == this.toString();
    }

    getTime() {
        return this.getHours() + ":" + this.getMinutes() + ":" + this.getSeconds();
    }

    toString() {
        return this.getYear() + "-" + this.getMonth() + "-" + this.getDay() + "T" + this.getHours() + ":" + this.getMinutes() + ":" + this.getSeconds();
    }
}

const LocalDateTime = {
    fromEpochSeconds: fromEpochSeconds,
    now: now
}

export default LocalDateTime;