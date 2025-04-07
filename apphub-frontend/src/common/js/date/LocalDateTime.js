import { hasValue, throwException } from "../Utils";

const fromEpochSeconds = (epoch) => {
    const d = new Date(0);
    d.setUTCSeconds(epoch);
    console.log(epoch, d);
    return new LocalDateTimeObj(d);
}

const fromLocalDateTime = (localDateTime) => {
    return create(new Date(localDateTime));
}

const create = (date) => {
    return new LocalDateTimeObj(date);
}

const now = () => {
    return create(new Date());
}

class LocalDateTimeObj {
    constructor(date) {
        if (!hasValue(date)) {
            throwException("IllegalArgument", "date must not be null");
        }

        if (!(date instanceof Date)) {
            throwException("IllegalArgument", "Date is not a Date");
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

    formatWithoutSeconds() {
        return this.getYear() + "-" + this.getMonth() + "-" + this.getDay() + " " + this.getHours() + ":" + this.getMinutes();
    }

    equals(obj) {
        if (!hasValue(obj)) {
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
    now: now,
    fromLocalDateTime: fromLocalDateTime,
}

export default LocalDateTime;