(function PageController(){
    scriptLoader.loadScript("/res/notebook/js/category_list_controller.js");

    $(document).ready(function(){
        eventProcessor.processEvent(new Event(events.LOAD_LOCALIZATION, {module: "notebook", fileName: "notebook"}));
    });
})();