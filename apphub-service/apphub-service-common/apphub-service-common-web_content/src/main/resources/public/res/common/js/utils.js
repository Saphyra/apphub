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

function displayNotificationNum(containerId, notificationNum){
    const container = document.getElementById(containerId);
        container.innerHTML = notificationNum;

    if(notificationNum == 0){
        container.parentNode.classList.remove("notification-container");
    }else{
        if(!container.parentNode.classList.contains("notification-container")){
            container.parentNode.classList.add("notification-container");
        }
    }
}

function setIntervalImmediate(callBack, interval){
    callBack();
    return setInterval(callBack, interval);
}

function createSpan(text){
    if(text == null || text == undefined){
        text = "";
    }
    const label = document.createElement("SPAN");
        label.innerHTML = text;
    return label;
}

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