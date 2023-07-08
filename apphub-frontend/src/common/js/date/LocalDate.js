import Utils from "../Utils";

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

const parse = (dateString) => {
    return new LocalDateObj(new Date(extractYear(dateString), extractMonth(dateString) - 1, extractDay(dateString)));
}

const create = (date) => {
    return new LocalDateObj(date);
}

const now = () => {
    return create(new Date());
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
        if (!Utils.hasValue(date)) {
            Utils.throwException("IllegalArgument", "date must not be null");
        }

        if (!(date instanceof Date)) {
            Utils.throwException("IllegalArgument", "Date is not a Date");
        }
        this.date = date;
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

    getDay() {
        return String(this.date.getDate()).padStart(2, '0');
    }

    format() {
        return this.getYear() + "-" + this.getMonth() + "-" + this.getDay();
    }

    isSameMonth(obj) {
        if (!obj instanceof LocalDateObj) {
            Utils.throwException("IllegalArgument", "obj is not a LocalDateObj");
        }

        return this.getYear() == obj.getYear() && this.getMonth() == obj.getMonth();
    }

    equals(obj) {
        if (!Utils.hasValue(obj)) {
            return false;
        }

        if (!obj instanceof LocalDateObj) {
            return false;
        }

        return obj.toString() == this.toString();
    }

    toString(){
        return this.getYear() + "-" + this.getMonth() + "-" + this.getDay();
    }
}

const LocalDate = {
    parse: parse,
    create: create,
    now: now,
};

export default LocalDate;