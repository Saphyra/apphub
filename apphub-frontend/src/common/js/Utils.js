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

const Utils = {
    getCookie: getCookie,
    setCookie: setCookie,
    getBrowserLanguage: getBrowserLanguage,
    throwException: throwException,
    getQueryParam: getQueryParam,
    isBlank: isBlank,
    generateRandomId: generateRandomId,
}

export default Utils;