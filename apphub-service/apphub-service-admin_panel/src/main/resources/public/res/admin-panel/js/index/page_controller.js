(function PageController(){
    scriptLoader.loadScript("/res/admin-panel/js/index/menu_controller.js");

    $(document).ready(function(){
        eventProcessor.processEvent(new Event(events.LOAD_LOCALIZATION, {module: "admin_panel", fileName: "index"}));
    });
})();