const COOKIE_LOCALE = "language";
const HEADER_BROWSER_LANGUAGE = "BrowserLanguage";
const DEFAULT_LOCALE = "hu";
const LINE_SEPARATOR = "\n";

(function ScriptLoader(){
    const date = getDate();
    const loadedScripts = [];

    window.scriptLoader = new function(){
        this.loadScript = loadScript;
    }

    //Platform
    scriptLoader.loadScript("/res/common/js/optional.js");
    scriptLoader.loadScript("/res/common/js/stream.js");
    scriptLoader.loadScript("/res/common/js/utils.js");
    scriptLoader.loadScript("/res/common/js/dao/dao.js");
    scriptLoader.loadScript("/res/common/js/dao/mapping.js");
    scriptLoader.loadScript("/res/common/js/dao/error_handler.js");
    scriptLoader.loadScript("/res/common/js/event_processor.js");
    scriptLoader.loadScript("/res/common/js/dom_builder.js");

    //App
    scriptLoader.loadScript("/res/common/js/notification_service.js");

    scriptLoader.loadScript("/res/common/js/page_loader.js");
    scriptLoader.loadScript("/res/common/js/localization.js");
    scriptLoader.loadScript("/res/common/js/logout_service.js");
    scriptLoader.loadScript("/res/common/js/session_check.js");

    /*
        Loads the script given as argument.
        Arguments:
            - src: The path of the requested script.
        Throws
            - IllegalArgument exception if src is null ord undefined
            - IllegalState exception if jQuery cannot be found.
    */
    function loadScript(src){
        if(src == undefined || src == null){
            throwException("IllegalArgument", "src must not be null or undefined.");
        }

        console.log("Loading script " + src);
        if(loadedScripts.indexOf(src) > -1){
            //console.log(src + " is already loaded.");
            return;
        }
        
        if($ == undefined){
            throwException("IllegalState", "jQuery cannot be resolved.");
        }
        $.ajax({
            async: false,
            url: src + "?" + date,
            dataType: "script",
            cache: true
        });
        loadedScripts.push(src);
    }

    function getDate(){
        const date = new Date();
        return date.getFullYear() + "-" + date.getMonth() + "-" + date.getDate() + "/" + date.getHours() + ":" + date.getMinutes();
    }
})();