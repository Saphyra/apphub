(function PageController(){

    $(document).ready(function(){
        init();
    });
    
    function init(){
        eventProcessor.processEvent(new Event(events.LOAD_LOCALIZATION, {module: "modules", fileName: "modules"}));
    }
})();