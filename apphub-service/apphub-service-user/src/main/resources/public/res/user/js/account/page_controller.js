(function PageController(){

    $(document).ready(function(){
        eventProcessor.processEvent(new Event(events.LOAD_LOCALIZATION, {module: "user", fileName: "account"}));
    });
})();