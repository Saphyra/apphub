import Constants from "./Constants";
import Optional from "./collection/Optional";
import Stream from "./collection/Stream";

export const spinnerWrappedOperation = (setDisplaySpinner, call) => {
    setDisplaySpinner(true);

    try {
        call();
    } finally {
        setDisplaySpinner(false);
    }
}

export const getCookie = (key) => {
    const cookies = document.cookie.split('; ');
    for (let cIndex in cookies) {
        const cookie = cookies[cIndex].split("=");
        if (cookie[0] === key) {
            return cookie[1];
        }
    }
    return null;
}

export const setCookie = (key, value, expirationDays) => {
    let cookieString = key + "=" + value;
    if (expirationDays !== null && expirationDays !== undefined) {
        const date = new Date();
        date.setTime(+ date + (expirationDays * 86400000))
        cookieString += ";expires=" + date.toGMTString();
    }
    cookieString += "; path=/";

    window.document.cookie = cookieString;
};

export const getBrowserLanguage = () => {
    return navigator.language.toLowerCase().split("-")[0];
}

export const throwException = (name, message) => {
    name = name === undefined ? "" : name;
    message = message === undefined ? "" : message;
    console.error(name + " - " + message);
    throw { name: name, message: message, stackTrace: (new Error()).stack };
}

export const getQueryParam = (paramName) => {
    return new URLSearchParams(window.location.search).get(paramName);
}

export const isBlank = (str) => {
    return "&nbsp;" == str || (!str || /^\s*$/.test(str));
}

export const generateRandomId = () => {
    var S4 = function () {
        return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
    };
    return (S4() + S4() + "-" + S4() + "-" + S4() + "-" + S4() + "-" + S4() + S4() + S4());
}

export const hasValue = (value) => {
    return value !== null && value !== undefined;
}

export const formatFileSize = (bytes) => {
    if (bytes > Constants.GIGABYTES) {
        return limitDecimals(bytes / Constants.GIGABYTES, 1) + " GB";
    }

    if (bytes > Constants.MEGABYTES) {
        return limitDecimals(bytes / Constants.MEGABYTES, 1) + " MB";
    }

    if (bytes > Constants.KILOBYTES) {
        return limitDecimals(bytes / Constants.KILOBYTES, 1) + " KB";
    }

    return bytes + " B";

    function limitDecimals(value, limit) {
        if (value % 1 == 0) {
            return value;
        }

        return parseFloat(value)
            .toFixed(limit);
    }
}

export const copyAndSet = (items, set) => {
    const copy = new Stream(items)
        .toList();
    set(copy);
}

export const addAndSet = (items, item, set) => {
    const copy = new Stream(items)
        .add(item)
        .toList();
    set(copy);
}

//Remove item if remove() == true
export const removeAndSet = (items, remove, set) => {
    const copy = new Stream(items)
        .remove(remove)
        .toList();
    set(copy);
}

export const bytesToMegabytes = (bytes) => {
    return Math.round(bytes / 1024 / 1024);
}

export const isTrue = (b) => {
    if (typeof b === "boolean") {
        return b;
    }

    if (typeof b === "string") {
        return b == "true";
    }

    return false;
}

export const nullIfEmpty = (input) => {
    if (!hasValue(input) || input.length === 0) {
        return null;
    }

    return input;
}

export const isJsonString = (input) => {
    try {
        JSON.parse(input);

        return true;
    } catch (e) {
        return false;
    }
}

export const numberOfDigits = (input) => {
    return Math.floor(Math.log(Math.abs(input)) * Math.LOG10E + 1 | 0);
}

export const roundPercent = (input) => {
    return Math.round(input * 100) / 100;
}

export const formatDuration = (nanoseconds) => {
    const milliseconds = nanoseconds / 1_000_000;

    if (milliseconds < 1000) {
        return milliseconds.toFixed(3).toLocaleString() + " ms";
    }

    const seconds = milliseconds / 1000;

    return seconds.toFixed(3).toLocaleString() + " s";
}

export const formatNumber = (number, digits) => {
    if (!hasValue(number)) {
        return number;
    }

    return number.toFixed(digits).toLocaleString("").replace(/\B(?=(\d{3})+(?!\d))/g, " ");
}

export const isArrayEmpty = (input) => {
    if (!hasValue(input)) {
        return true;
    }

    if (!Array.isArray(input)) {
        throwException("IllegalArgument", "Input must be an array");
    }

    return input.length == 0;
}

export const cacheAndUpdate = (key, value, callback, convertValue = v => v) => {
    if (hasValue(value)) {
        sessionStorage[key] = convertValue(value);
    } else {
        delete sessionStorage[key];
    }

    callback(value);
}

export const cachedOrDefault = (key, defaultValue = "", convertValue = v => v) => {
    return hasValue(sessionStorage[key]) ? convertValue(sessionStorage[key]) : defaultValue;
}

export const validate = (predicate, errorMessage) => {
    if (!predicate()) {
        return errorMessage;
    }

    return null;
}

export const validateAll = (validations = []) => {
    return new Stream(validations)
        .map(validation => validation())
        .filter(validationResult => hasValue(validationResult))
        .findFirst()
        .orElse(null);
}

export const randomNumber = (min, max) => {
    return Math.floor(Math.random() * (max - min + 1) + min)
}

export const mapOrDefault = (value, defaultValue, mapper = v => v) => {
    return new Optional(value)
        .map(mapper)
        .orElse(defaultValue);
}