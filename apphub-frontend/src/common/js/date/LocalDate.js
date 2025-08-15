import { type } from "@testing-library/user-event/dist/type";
import { hasValue, throwException } from "../Utils";
import { DAYS_OF_WEEK } from "./DayOfWeek";

const MILLISECS_IN_SECOND = 1000;
const MILLISECS_IN_MINUTE = 60 * MILLISECS_IN_SECOND;
const MILLISECS_IN_HOUR = 60 * MILLISECS_IN_MINUTE;
const MILLISECS_IN_DAY = 24 * MILLISECS_IN_HOUR;

export const DAYS_IN_WEEK = 7;

Date.isLeapYear = function (year) {
    return (((year % 4 === 0) && (year % 100 !== 0)) || (year % 400 === 0));
};

Date.getDaysInMonth = function (year, month) {
    return [31, (Date.isLeapYear(year) ? 29 : 28), 31, 30, 31, 30, 31, 31, 30, 31, 30, 31][month];
};

Date.prototype.isLeapYear = function () {
    return Date.isLeapYear(this.getFullYear());
};

Date.prototype.getDaysInMonth = function () {
    return Date.getDaysInMonth(this.getFullYear(), this.getMonth());
};

Date.prototype.plusMonths = function (value) {
    const date = new Date(this.valueOf());
    const n = this.getDate();
    date.setDate(1);
    date.setMonth(this.getMonth() + value);
    date.setDate(Math.min(n, date.getDaysInMonth()));
    return date;
};

Date.prototype.minusMonths = function (months) {
    const date = new Date(this.valueOf());
    const n = this.getDate();
    date.setDate(1);
    date.setMonth(date.getMonth() - months);
    date.setDate(Math.min(n, date.getDaysInMonth()));
    return date;
}

Date.prototype.plusDays = function (days) {
    const result = new Date(this);
    result.setDate(result.getDate() + days);
    return result;
}

Date.prototype.minusDays = function (days) {
    const result = new Date(this);
    result.setDate(result.getDate() - days);
    return result;
}

Date.prototype.getWeekOfYear = function () {
    var d = new Date(Date.UTC(this.getFullYear(), this.getMonth(), this.getDate()));
    var dayNum = d.getUTCDay() || 7;
    d.setUTCDate(d.getUTCDate() + 4 - dayNum);
    var yearStart = new Date(Date.UTC(d.getUTCFullYear(), 0, 1));
    return Math.ceil((((d - yearStart) / 86400000) + 1) / 7)
}

const parse = (dateString) => {
    if (dateString instanceof LocalDateObj) {
        return dateString;
    }
    const type = typeof dateString;

    if (type !== "string") {
        throwException("IllegalArgument", "date must be string, it was " + type);
    }

    return new LocalDateObj(new Date(extractYear(dateString), extractMonth(dateString) - 1, extractDay(dateString)));
}

const create = (date) => {
    return new LocalDateObj(date);
}

const now = () => {
    return create(new Date());
}

//Month is 0-based!
const of = (year, month, day) => {
    return create(new Date(year, month, day));
}

const extractDay = (date) => {
    return date.split("-")[2];
}

const extractMonth = (date) => {
    return date.split("-")[1];
}

const extractYear = (date) => {
    return date.split("-")[0];
}

class LocalDateObj {
    constructor(date) {
        if (!hasValue(date)) {
            throwException("IllegalArgument", "date must not be null");
        }

        if (!(date instanceof Date)) {
            throwException("IllegalArgument", "Date is not a Date");
        }
        this.date = date;
    }

    plusDays(d) {
        return new LocalDateObj(this.date.plusDays(d));
    }

    minusDays(d) {
        return new LocalDateObj(this.date.minusDays(d));
    }

    plusMonths(m) {
        return new LocalDateObj(this.date.plusMonths(m));
    }

    minusMonths(m) {
        return new LocalDateObj(this.date.minusMonths(m));
    }

    getMonth() {
        return String(this.date.getMonth() + 1).padStart(2, '0'); //January is 0!
    }

    getYear() {
        return this.date.getFullYear();
    }

    getWeekOfYear() {
        return this.date.getWeekOfYear();
    }

    getDayOfWeek() {
        let d = this.date.getDay();

        if (d === 0) {
            d = 7;
        }

        return DAYS_OF_WEEK[d - 1];
    }

    getDay() {
        return String(this.date.getDate()).padStart(2, '0');
    }

    format() {
        return this.getYear() + "-" + this.getMonth() + "-" + this.getDay();
    }

    isSameMonth(obj) {
        if (!obj instanceof LocalDateObj) {
            throwException("IllegalArgument", "obj is not a LocalDateObj");
        }

        return this.getYear() == obj.getYear() && this.getMonth() == obj.getMonth();
    }

    isBefore(obj){
        if (!obj instanceof LocalDateObj) {
            throwException("IllegalArgument", "obj is not a LocalDateObj");
        }

        const newDate = new Date(this.date);
        const newObj = new Date(obj.date);

        newDate.setHours(0, 0, 0, 0);
        newObj.setHours(0, 0, 0, 0);

        return newDate < newObj;
    }

    equals(obj) {
        if (!hasValue(obj)) {
            return false;
        }

        if (!obj instanceof LocalDateObj) {
            return false;
        }

        return obj.toString() == this.toString();
    }

    toString() {
        return this.getYear() + "-" + this.getMonth() + "-" + this.getDay();
    }

    isBeforeInclusive(other) {
        return this.toString() <= other.toString();
    }
}

const LocalDate = {
    parse: parse,
    create: create,
    now: now,
    of: of,
};

export default LocalDate;