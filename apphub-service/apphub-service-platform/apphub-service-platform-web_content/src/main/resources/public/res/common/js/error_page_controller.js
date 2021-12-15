(function PageController(){
    scriptLoader.loadScript("/res/common/js/logout_service.js");

    $(document).ready(function(){
        eventProcessor.processEvent(new Event(events.LOAD_LOCALIZATION, {module: "common", fileName: "error"}));
    });
})();