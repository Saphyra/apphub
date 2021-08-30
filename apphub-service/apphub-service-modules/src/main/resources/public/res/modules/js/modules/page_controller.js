scriptLoader.loadScript("/res/common/js/localization/custom_localization.js");
scriptLoader.loadScript("/res/modules/js/modules/modules_controller.js");

(function PageController(){
    $(document).ready(function(){
        eventProcessor.processEvent(new Event(events.LOAD_LOCALIZATION, {module: "modules", fileName: "modules"}));
    });
})();