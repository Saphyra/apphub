(function PageController(){
    scriptLoader.loadScript("/res/modules/js/modules/modules_controller.js");

    events.SEARCH_ATTEMPT = "search-attempt";

    $(document).ready(function(){
        eventProcessor.processEvent(new Event(events.LOAD_LOCALIZATION, {module: "modules", fileName: "modules"}));

        $("#search-field").on("keyup", function(){
            eventProcessor.processEvent(new Event(events.SEARCH_ATTEMPT));
        });
    });

    eventProcessor.registerProcessor(new EventProcessor(
        function(eventType){return eventType == events.LOCALIZATION_LOADED},
        function(){
            modulesController.displayModules();
        },
        true
    ));
})();