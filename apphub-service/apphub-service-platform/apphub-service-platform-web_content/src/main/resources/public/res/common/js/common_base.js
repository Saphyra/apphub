const COOKIE_LOCALE = "language";
const HEADER_BROWSER_LANGUAGE = "BrowserLanguage";

(function ScriptLoader(){
    const date = getDate();
    const loadedScripts = [];

    window.scriptLoader = new function(){
        this.loadScript = loadScript;
    }
    
    scriptLoader.loadScript("/res/common/js/optional.js");
    scriptLoader.loadScript("/res/common/js/stream.js");
    scriptLoader.loadScript("/res/common/js/utils.js");
    scriptLoader.loadScript("/res/common/js/log_service.js");
    scriptLoader.loadScript("/res/common/js/dao/dao.js");
    scriptLoader.loadScript("/res/common/js/dao/mapping.js");
    scriptLoader.loadScript("/res/common/js/dao/error_handler.js");
    scriptLoader.loadScript("/res/common/js/event_processor.js");
    initPageLoader();
    scriptLoader.loadScript("/res/common/js/notification_service.js");
    
    scriptLoader.loadScript("/res/common/js/localization/localization_loader.js");
    scriptLoader.loadScript("/res/common/js/localization/localization.js");
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

        //console.log("Loading script " + src);
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

function initPageLoader(){
    const loaders = [];

    window.pageLoader = new function(){
        this.addLoader = function(loader, description){
            console.log("Adding loader " + description);
            if(!hasValue(description)){
                throwException("IllegalArgument", "Description must not be null or undefined.");
            }

            if(!isFunction(loader)){
                throwException("IllegalArgument", "Loader is not a function.");
            }

            loaders.push({loader: loader, description: description});
        }
    }

    eventProcessor.registerProcessor(new EventProcessor(
        function(eventType){return eventType == events.LOCALIZATION_LOADED},
        function(){
            new Stream(loaders)
                .forEach(
                    function(loader){
                        setTimeout(
                            function(){
                                logService.logToConsole("Calling loader: " + loader.description);
                                loader.loader();
                            },
                            0
                        )
                    }
                );
        },
        true,
        "Page loaders"
    ));
}