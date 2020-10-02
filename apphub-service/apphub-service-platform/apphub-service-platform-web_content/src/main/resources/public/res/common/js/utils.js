function hasValue(obj){
    return obj != undefined && obj != null;
}

function throwException(name, message){
    name = name == undefined ? "" : name;
    message = message == undefined ? "" : message;
    throw {name: name, message: message};
}

function getLocale(){
    return getCookie(COOKIE_LOCALE) || getBrowserLanguage();
}

function getBrowserLanguage(){
    return navigator.language.toLowerCase().split("-")[0];
}

function isEmailValid(email){
    let result;
    if(email == null || email == undefined){
        result = false;
    }

    if(email.length < 6){
        return false;
    }

    return email.match("^\\s*[\\w\\-\\+_]+(\\.[\\w\\-\\+_]+)*\\@[\\w\\-\\+_]+\\.[\\w\\-\\+_]+(\\.[\\w\\-\\+_]+)*\\s*$");
}

function getActualTimeStamp(){
    return Math.floor(new Date().getTime() / 1000);
}

function switchTab(clazz, id, duration){
    $("." + clazz).hide(duration);
    $("#" + id).show(duration);
}

function setIntervalImmediate(callBack, interval){
    callBack();
    return setInterval(callBack, interval);
}

function setCookie(key, value, expirationDays) {
    let cookieString = key + "=" + value;
    if(hasValue(expirationDays)){
        const date = new Date();
        date.setTime(+ date + (expirationDays * 86400000))
        cookieString += ";expires=" + date.toGMTString();
    }
    cookieString += "; path=/";

    window.document.cookie = cookieString;
};

function getCookie(key){
    const cookies = document.cookie.split('; ');
    for(let cIndex in cookies){
        const cookie = cookies[cIndex].split("=");
        if(cookie[0] == key){
            return cookie[1];
        }
    }
    return null;
}

function createSvgElement(type){
    return document.createElementNS("http://www.w3.org/2000/svg", type);
}

function generateRandomId() {
    var S4 = function() {
       return (((1+Math.random())*0x10000)|0).toString(16).substring(1);
    };
    return (S4()+S4()+"-"+S4()+"-"+S4()+"-"+S4()+"-"+S4()+S4()+S4());
}

function selectElementText(el, win) {
     win = win || window;
     const doc = win.document;
     if (win.getSelection && doc.createRange) {
         const sel = win.getSelection();
         const range = doc.createRange();
         range.selectNodeContents(el);
         sel.removeAllRanges();
         sel.addRange(range);
     } else if (doc.body.createTextRange) {
         const range = doc.body.createTextRange();
         range.moveToElementText(el);
         range.select();
     }
 }

function clearSelection() {
    document.execCommand('selectAll', false, null);
}

function getValidationTimeout(){
    const presetTimeout = getCookie("validation-timeout");
    return presetTimeout == null ? 1000 : Number(presetTimeout);
}

function getQueryParam(paramName){
    return new URLSearchParams(window.location.search).get(paramName);
}

function search(arr, predicate){
    for(let i = 0; i < arr.length; i++){
        if(predicate(arr[i])){
            return i;
        }
    }

    return null;
}