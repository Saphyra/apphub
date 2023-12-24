import Constants from "./Constants";
import Stream from "./collection/Stream";

const getCookie = (key) => {
    const cookies = document.cookie.split('; ');
    for (let cIndex in cookies) {
        const cookie = cookies[cIndex].split("=");
        if (cookie[0] === key) {
            return cookie[1];
        }
    }
    return null;
}

const setCookie = (key, value, expirationDays) => {
    let cookieString = key + "=" + value;
    if (expirationDays !== null && expirationDays !== undefined) {
        const date = new Date();
        date.setTime(+ date + (expirationDays * 86400000))
        cookieString += ";expires=" + date.toGMTString();
    }
    cookieString += "; path=/";

    window.document.cookie = cookieString;
};

const getBrowserLanguage = () => {
    return navigator.language.toLowerCase().split("-")[0];
}

const throwException = (name, message) => {
    name = name === undefined ? "" : name;
    message = message === undefined ? "" : message;
    throw { name: name, message: message, stackTrace: (new Error()).stack };
}

const getQueryParam = (paramName) => {
    return new URLSearchParams(window.location.search).get(paramName);
}

const isBlank = (str) => {
    return "&nbsp;" == str || (!str || /^\s*$/.test(str));
}

const generateRandomId = () => {
    var S4 = function () {
        return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
    };
    return (S4() + S4() + "-" + S4() + "-" + S4() + "-" + S4() + "-" + S4() + S4() + S4());
}

const hasValue = (value) => {
    return value !== null && value !== undefined;
}

const formatFileSize = (bytes) => {
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

const copyAndSet = (items, set) => {
    const copy = new Stream(items)
        .toList();
    set(copy);
}

const addAndSet = (items, item, set) => {
    const copy = new Stream(items)
        .add(item)
        .toList();
    set(copy);
}

const removeAndSet = (items, remove, set) => {
    const copy = new Stream(items)
        .remove(remove)
        .toList();
    set(copy);
}

const bytesToMegabytes = (bytes) => {
    return Math.round(bytes / 1024 / 1024);
}

const isTrue = (b) => {
    if (typeof b === "boolean") {
        return b;
    }

    if (typeof b === "string") {
        return b == "true";
    }

    return false;
}

const Utils = {
    getCookie: getCookie,
    setCookie: setCookie,
    getBrowserLanguage: getBrowserLanguage,
    throwException: throwException,
    getQueryParam: getQueryParam,
    isBlank: isBlank,
    generateRandomId: generateRandomId,
    hasValue: hasValue,
    formatFileSize: formatFileSize,
    bytesToMegabytes: bytesToMegabytes,
    copyAndSet: copyAndSet,
    addAndSet: addAndSet,
    removeAndSet: removeAndSet,
    isTrue: isTrue,
}

export default Utils;